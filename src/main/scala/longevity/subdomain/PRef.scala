package longevity.subdomain

import longevity.persistence.PersistedAssoc
import longevity.subdomain.root.KeyVal

/** an indirect reference to a persistent object. they can typically only be
 * dereferenced via the [[longevity.persistence persistence layer]].
 *
 * there are two kinds of persistent refs: [[Assoc associations]] and
 * [[longevity.subdomain.root.KeyVal key values]]. we hope to integrate these
 * two types more in the future. in particular, it should be easier to embed a
 * key value of another aggregate in an entity, in place of embedding an
 * association.
 */
trait PRef[R <: Root] {

  /** prevent subtyping outside of longevity library */
  private[longevity] val _lock: Int

  // this will never ClassCastException because there are only these three kinds
  private[longevity] def pattern: PRef.Pattern[R] =
    if (this.isInstanceOf[UnpersistedAssoc[R]]) {
      PRef.UAssocPattern(this.asInstanceOf[UnpersistedAssoc[R]])
    } else if (this.isInstanceOf[PersistedAssoc[R]]) {
      PRef.PAssocPattern(this.asInstanceOf[PersistedAssoc[R]])
    } else {
      PRef.KeyValPattern(this.asInstanceOf[KeyVal[R]])
    }

}

/** match pattern support for persistent refs */
private[longevity] object PRef {

  sealed trait Pattern[R <: Root]
  case class UAssocPattern[R <: Root](assoc: UnpersistedAssoc[R]) extends Pattern[R]
  case class PAssocPattern[R <: Root](assoc: PersistedAssoc[R]) extends Pattern[R]
  case class KeyValPattern[R <: Root](keyVal: KeyVal[R]) extends Pattern[R]

}
