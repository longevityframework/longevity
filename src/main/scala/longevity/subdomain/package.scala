package longevity

import longevity.subdomain.persistent.Persistent

/** provides support for constructing your subdomain */
package object subdomain {

  /** an [[Assoc association]] to an unspecified type of [[Persistent persistent
   * entity]]. this is useful for building stuff from `emblem.traversors` for
   * traversing entities.
   * 
   * leaving this `private[longevity]` for now, but if any user-facing use-case
   * comes up, we can expose it.
   */
  private[longevity] type AssocAny = Assoc[_ <: Persistent]

}
