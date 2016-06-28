package longevity.subdomain.realized

import emblem.emblematic.Emblematic
import emblem.typeBound.TypeBoundMap
import longevity.subdomain.KeyVal
import longevity.subdomain.persistent.Persistent
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

  type PKey[A <: KeyVal[P]] = Key[P, A]
  type PRealizedKey[A <: KeyVal[P]] = RealizedKey[P, A]

  // TODO is this TBM used? could i just have the keySet instead?
  val realizedKeys: TypeBoundMap[KeyVal[P], PKey, PRealizedKey] = {
    pType.keySet.foldLeft(TypeBoundMap[KeyVal[P], PKey, PRealizedKey]()) { (acc, key) =>
      def pair[A <: KeyVal[P]](key: Key[P, A]) = {
        val prop = key.keyValProp
        val realizedKey = RealizedKey[P, A](
          key)(
          realizedProps(prop).asInstanceOf[RealizedProp[P, A]],
          emblematic)(
          prop.propTypeKey)
        acc + (key -> realizedKey)
      }
      pair(key)
    }
  }

  val keySet: Set[AnyRealizedKey[P]] = realizedKeys.values.toSet

}
