package longevity.subdomain

import longevity.persistence.PersistedAssoc
import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.KeyVal

/** an indirect reference to a persistent object. they can typically only be
 * dereferenced via the [[longevity.persistence persistence layer]].
 *
 * there are two kinds of persistent refs: [[Assoc associations]] and
 * [[longevity.subdomain.ptype.KeyVal key values]]. we hope to integrate these
 * two types more in the future. in particular, it should be easier to embed a
 * key value of another aggregate in an entity, in place of embedding an
 * association.
 */
trait PRef[P <: Persistent] {

  /** prevent subtyping outside of longevity library */
  private[longevity] val _lock: Int

  // this will never ClassCastException because there are only these three kinds
  private[longevity] def pattern: PRef.Pattern[P] =
    if (this.isInstanceOf[UnpersistedAssoc[P]]) {
      PRef.UAssocPattern(this.asInstanceOf[UnpersistedAssoc[P]])
    } else if (this.isInstanceOf[PersistedAssoc[P]]) {
      PRef.PAssocPattern(this.asInstanceOf[PersistedAssoc[P]])
    } else {
      PRef.KeyValPattern(this.asInstanceOf[KeyVal[P]])
    }

}

/** match pattern support for persistent refs */
private[longevity] object PRef {

  sealed trait Pattern[P <: Persistent]
  case class UAssocPattern[P <: Persistent](assoc: UnpersistedAssoc[P]) extends Pattern[P]
  case class PAssocPattern[P <: Persistent](assoc: PersistedAssoc[P]) extends Pattern[P]
  case class KeyValPattern[P <: Persistent](keyVal: KeyVal[P]) extends Pattern[P]

}
