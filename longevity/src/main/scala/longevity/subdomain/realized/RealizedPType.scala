package longevity.subdomain.realized

import emblem.TypeKey
import emblem.emblematic.EmblemProp
import emblem.emblematic.Emblematic
import emblem.emblematic.EmblematicPropPath
import emblem.typeBound.TypeBoundMap
import emblem.typeKey
import longevity.exceptions.subdomain.DuplicateKeyException
import longevity.exceptions.subdomain.InvalidPartitionException
import longevity.subdomain.DerivedPType
import longevity.subdomain.KeyVal
import longevity.subdomain.PType
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.PartitionKey
import longevity.subdomain.ptype.Prop

private[longevity] class RealizedPType[P](
  pType: PType[P],
  polyPTypeOpt: Option[PType[_ >: P]],
  emblematic: Emblematic) {

  private val postPartitionProps: Seq[Prop[P, _]] = pType.partitionKey match {
    case Some(key) => postPartitionProps(key)
    case None => Seq()
  }

  private type MyProp[A] = Prop[P, A]
  private type MyRealizedProp[A] = RealizedProp[P, A]

  private val myRealizedProps: TypeBoundMap[Any, MyProp, MyRealizedProp] = {
    val empty = TypeBoundMap[Any, MyProp, MyRealizedProp]()
    def myProps = pType.propSet ++ postPartitionProps.toSet
    myProps.foldLeft(empty) { (acc, prop) =>
      def pair[A](prop: Prop[P, A]) = acc + (prop -> RealizedProp(prop, emblematic))
      pair(prop)
    }
  }

  type PProp[A] = Prop[_ >: P, A]
  type PRealizedProp[A] = RealizedProp[_ >: P, A]

  val realizedProps: TypeBoundMap[Any, PProp, PRealizedProp] = {
    def myWidenedProps = myRealizedProps.widen[PProp, PRealizedProp]
    pType match {
      case derivedPType: DerivedPType[P, _] =>
        def polyProps[PP >: P](polyPTypeKey: TypeKey[PP]) = {
          val empty = TypeBoundMap[Any, PProp, PRealizedProp]()
          polyPTypeOpt match {
            case None => empty
            case Some(polyPType) => polyPType.propSet.foldLeft(empty) { (acc, prop) =>
              def pair[PP >: P, A](prop: Prop[PP, A]) = {
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
      def kvtk = key.keyValProp.propTypeKey
      if (acc.contains(kvtk)) throw new DuplicateKeyException()(pType.pTypeKey, kvtk)

      def accumulateKey[V <: KeyVal[P, V]](key: Key[P, V]) = acc + (kvtk -> realizedKeyForKey(key))

      def accumulatePKey[V <: KeyVal[P, V]](key: PartitionKey[P, V]) =
        acc + (kvtk -> realizedKeyForPartitionKey(key))

      pType.partitionKey match {
        case Some(pk) if pk == key => accumulatePKey(pk)
        case _ => accumulateKey(key)
      }
    }
  }

  def realizedKey[V <: KeyVal[P, V] : TypeKey]: RealizedKey[P, V] = {
    realizedKeyMap(typeKey[V]).asInstanceOf[RealizedKey[P, V]]
  }

  val keySet: Set[AnyRealizedKey[P]] = realizedKeyMap.values.toSet

  val partitionKey: Option[AnyRealizedPartitionKey[P]] = realizedKeyMap.values.collectFirst {
    case pk: AnyRealizedPartitionKey[P] => pk
  }

  private def postPartitionProps[V  <: KeyVal[P, V]](key: PartitionKey[P, V]): Seq[Prop[P, _]] = {
    postPartitionPropInfos[V](key)(key.keyValTypeKey).map(_.prop)
  }

  private def realizedKeyForKey[V <: KeyVal[P, V]](key: Key[P, V]): RealizedKey[P, V] = {
    val prop: Prop[P, V] = key.keyValProp
    new RealizedKey[P, V](key, myRealizedProps(prop))(prop.propTypeKey)
  }

  private def realizedKeyForPartitionKey[V <: KeyVal[P, V]](key: PartitionKey[P, V]): RealizedKey[P, V] = {
    val vTypeKey = key.keyValTypeKey
    val prop = myRealizedProps(key.keyValProp)
    val pppis = postPartitionPropInfos(key)(vTypeKey)
    val realizedPartitionProps = key.partition.props.map(myRealizedProps(_)) 
    val realizedPostPartitionProps = pppis.map(_.prop).map(myRealizedProps(_))
    val emblematicPropPaths = {
      def p2ppi[B](prop: Prop[P, B]) = {
        if (prop == key.keyValProp) {
          EmblematicPropPath.empty[V](vTypeKey)
        } else {
          val keyPropPath = prop.path.drop(key.keyValProp.path.size + 1)
          EmblematicPropPath[V, B](emblematic, keyPropPath)(vTypeKey, prop.propTypeKey)
        }
      }
      def pepps  = key.partition.props.map { prop => p2ppi(prop) }
      def ppepps = pppis.map(_.emblematicPropPath)
      pepps ++ ppepps
    }
    RealizedPartitionKey[P, V](
      key,
      prop,
      realizedPartitionProps,
      realizedPostPartitionProps,
      emblematicPropPaths)(
      pType.pTypeKey,
      prop.propTypeKey)
  }

  /** what we need to know about a properties that are within the partition key,
   * but are not part of the partition
   */
  private def postPartitionPropInfos[V <: KeyVal[P, V] : TypeKey](
    key: PartitionKey[P, V])
  : Seq[PostPartitionPropInfo[V, _]] = {

    implicit val pTypeKey = pType.pTypeKey
    val vTypeKey = implicitly[TypeKey[V]]

    def ppis(keyProps: Seq[PostPartitionPropInfo[V, _]], partitionProps: Seq[Prop[P, _]])
    : Seq[PostPartitionPropInfo[V, _]] = {
      if (partitionProps.isEmpty) {
        keyProps
      } else if (keyProps.isEmpty) {
        throw new InvalidPartitionException[P](key)(pTypeKey)
      } else if (keyProps.head.prop == partitionProps.head) {
        ppis(keyProps.tail, partitionProps.tail)
      } else {
        val headProp = keyProps.head.prop
        val emblem = emblematic.emblems.getOrElse(
          throw new InvalidPartitionException[P](key)(pTypeKey))(
          headProp.propTypeKey)
        val headSubProps = emblem.props.map { emblemProp =>
          def ppi[B](emblemProp: EmblemProp[_, B]) = {
            val fullPropPath = s"${headProp.path}.${emblemProp.name}"
            val keyPropPath = fullPropPath.drop(key.keyValProp.path.size + 1)
            PostPartitionPropInfo[V, B](
              Prop[P, B](fullPropPath, pTypeKey, emblemProp.typeKey),
              EmblematicPropPath[V, B](emblematic, keyPropPath)(vTypeKey, emblemProp.typeKey))
          }
          ppi(emblemProp)
        }
        ppis(headSubProps ++ keyProps.tail, partitionProps)
      }
    }

    val ppi = PostPartitionPropInfo(key.keyValProp, EmblematicPropPath.empty(vTypeKey))
    ppis(Seq(ppi), key.partition.props)
  }

  /** what we need to know about a property that is within the partition key,
   * but is not part of the partition
   */
  private case class PostPartitionPropInfo[V <: KeyVal[P, V], B](
    prop: Prop[P, B],
    emblematicPropPath: EmblematicPropPath[V, B])

}
