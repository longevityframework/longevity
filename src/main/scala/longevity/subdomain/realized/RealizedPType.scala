package longevity.subdomain.realized

import emblem.emblematic.Emblematic
import emblem.typeBound.TypeBoundMap
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.AnyKey
import longevity.subdomain.ptype.DerivedPType
import longevity.subdomain.ptype.Key
import longevity.subdomain.ptype.PType
import longevity.subdomain.ptype.Prop

class RealizedPType[P <: Persistent](
  pType: PType[P],
  emblematic: Emblematic) {

  // TODO should this just be Prop[P,A]??
  type PProp[A] = Prop[_ >: P <: Persistent, A]
  type PRealizedProp[A] = RealizedProp[_ >: P <: Persistent, A]

  val realizedProps: TypeBoundMap[Any, PProp, PRealizedProp] = {
    val props = pType match {
      case dpt: DerivedPType[_, _] => pType.propSet.toSet[PProp[_]] ++ dpt.polyPType.propSet
      case _ => pType.propSet
    }
    props.foldLeft(TypeBoundMap[Any, PProp, PRealizedProp]()) { (acc, prop) =>
      def pair[PP >: P <: Persistent, A](prop: Prop[PP, A]) = {
        acc + (prop -> RealizedProp(prop, emblematic))
      }
      pair(prop)
    }
  }

  def realizedProp(prop: Prop[_ >: P <: Persistent, _])
  : RealizedProp[_ >: P <: Persistent, _] = {
    def rp[A](prop: Prop[_ >: P <: Persistent, A]) = realizedProps(prop)
    rp(prop)
  }

  val realizedKeyMap: Map[AnyKey[P], AnyRealizedKey[P]] = {
    val empty = Map[AnyKey[P], AnyRealizedKey[P]]()
    pType.keySet.foldLeft(empty) { (acc, key) =>
      def accumulate[A <: KeyVal[P, A]](key: Key[P, A]) = {
        val prop = key.keyValProp
        val realizedKey = RealizedKey[P, A](
          key)(
          realizedProps(prop).asInstanceOf[RealizedProp[P, A]], // TODO asInstanceOf
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
