package longevity.subdomain.realized

import emblem.emblematic.Emblematic
import emblem.typeBound.TypeBoundMap
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.AnyKey
import longevity.subdomain.ptype.DerivedPType
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.PType
import longevity.subdomain.ptype.PolyPType
import longevity.subdomain.ptype.Prop

private[longevity] class RealizedPType[P <: Persistent](
  pType: PType[P],
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
        def polyProps[PP >: P <: Persistent](polyPType: PolyPType[PP]) = {
          val empty = TypeBoundMap[Any, PProp, PRealizedProp]()
          polyPType.propSet.foldLeft(empty) { (acc, prop) =>
            def pair[PP >: P <: Persistent, A](prop: Prop[PP, A]) = {
              acc + (prop -> RealizedProp(prop, emblematic))
            }
            pair(prop)
          }
        }
        myWidenedProps ++ polyProps(derivedPType.polyPType)
      case _ => myWidenedProps
    }
  }

  private val realizedKeyMap: Map[AnyKey[P], AnyRealizedKey[P]] = {
    val empty = Map[AnyKey[P], AnyRealizedKey[P]]()
    pType.keySet.foldLeft(empty) { (acc, key) =>
      def accumulate[A <: KeyVal[P, A]](key: Key[P, A]) = {
        val prop: Prop[P, A] = key.keyValProp
        val realizedKey = RealizedKey[P, A](key)(
          myRealizedProps(prop),
          emblematic)(
          prop.propTypeKey)
        acc + (key -> realizedKey)
      }
      accumulate(key)
    }
  }

  def realizedKeys[V <: KeyVal[P, V]](key: Key[P, V]): RealizedKey[P, V] =
    realizedKeyMap(key).asInstanceOf[RealizedKey[P, V]]

  val keySet: Set[AnyRealizedKey[P]] = realizedKeyMap.values.toSet

}
