package longevity.subdomain.realized

import emblem.TypeKey
import emblem.emblematic.Emblematic
import emblem.typeBound.TypeBoundMap
import emblem.typeKey
import longevity.exceptions.subdomain.DuplicateKeyException
import longevity.subdomain.DerivedPType
import longevity.subdomain.KeyVal
import longevity.subdomain.PType
import longevity.subdomain.Persistent
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.PartitionKey
import longevity.subdomain.ptype.Prop

private[longevity] class RealizedPType[P <: Persistent](
  pType: PType[P],
  polyPTypeOpt: Option[PType[_ >: P <: Persistent]],
  emblematic: Emblematic) {

  private type MyProp[A] = Prop[P, A]
  private type MyRealizedProp[A] = RealizedProp[P, A]

  private val myRealizedProps: TypeBoundMap[Any, MyProp, MyRealizedProp] = {
    val empty = TypeBoundMap[Any, MyProp, MyRealizedProp]()
    pType.propSet.foldLeft(empty) { (acc, prop) =>
      def pair[A](prop: Prop[P, A]) = {
        acc + (prop -> RealizedProp(prop, emblematic))
      }
      pair(prop)
    }
  }

  type PProp[A] = Prop[_ >: P <: Persistent, A]
  type PRealizedProp[A] = RealizedProp[_ >: P <: Persistent, A]

  val realizedProps: TypeBoundMap[Any, PProp, PRealizedProp] = {
    def myWidenedProps = myRealizedProps.widen[PProp, PRealizedProp]
    pType match {
      case derivedPType: DerivedPType[P, _] =>
        def polyProps[PP >: P <: Persistent](polyPTypeKey: TypeKey[PP]) = {
          val empty = TypeBoundMap[Any, PProp, PRealizedProp]()
          polyPTypeOpt match {
            case None => empty
            case Some(polyPType) => polyPType.propSet.foldLeft(empty) { (acc, prop) =>
              def pair[PP >: P <: Persistent, A](prop: Prop[PP, A]) = {
                acc + (prop -> RealizedProp(prop, emblematic))
              }
              pair(prop)
            }
          }
        }
        myWidenedProps ++ polyProps(derivedPType.polyPTypeKey)
      case _ => myWidenedProps
    }
  }

  private val realizedKeyMap: Map[TypeKey[_], AnyRealizedKey[P]] = {
    val empty = Map[TypeKey[_], AnyRealizedKey[P]]()
    pType.keySet.foldLeft(empty) { (acc, key) =>
      def accumulate[V <: KeyVal[P, V]](key: Key[P, V]) = {
        val realizedKey = realizedKeyForKey(key)
        def keyValTypeKey = key.keyValProp.propTypeKey
        if (acc.contains(keyValTypeKey)) {
          throw new DuplicateKeyException()(pType.pTypeKey, keyValTypeKey)
        }
        acc + (keyValTypeKey -> realizedKey)
      }
      accumulate(key)
    }
  }

  private def realizedKeyForKey[V <: KeyVal[P, V]](key: Key[P, V]): RealizedKey[P, V] = {
    val prop: Prop[P, V] = key.keyValProp
    key match {
      case pk: PartitionKey[P, V] =>
        val partitionProps = pk.partition.props.map { prop => myRealizedProps(prop) }
        new RealizedPartitionKey[P, V](
          pk,
          myRealizedProps(prop),
          partitionProps)(
          pType.pTypeKey,          
          prop.propTypeKey)
      case _ =>
        new RealizedKey[P, V](
          key,
          myRealizedProps(prop))(
          prop.propTypeKey)
    }
  }

  def realizedKey[V <: KeyVal[P, V] : TypeKey]: RealizedKey[P, V] = {
    realizedKeyMap(typeKey[V]).asInstanceOf[RealizedKey[P, V]]
  }

  val keySet: Set[AnyRealizedKey[P]] = realizedKeyMap.values.toSet

  val partitionKey: Option[AnyRealizedPartitionKey[P]] = realizedKeyMap.values.collectFirst {
    case pk: AnyRealizedPartitionKey[P] => pk
  }

}
