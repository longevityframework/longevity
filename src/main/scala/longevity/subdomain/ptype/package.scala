package longevity.subdomain

import emblem.TypeKeyMap
import longevity.subdomain.persistent.Persistent

package object ptype {

  /** a type key map of [[longevity.subdomain.persistent.Persistent
   * Persistent]] to [[PType]]
   * @see emblem.TypeKeyMap
   */
  type PTypePool = TypeKeyMap[Persistent, PType]

  // TODO scaladoc
  // TODO name this better
  type AnyKey[P <: Persistent] = Key[P, V] forSome { type V <: KeyVal[P] }

}
