package longevity.subdomain

import emblem.imports.TypeKeyMap
import longevity.subdomain.persistent.Persistent

package object ptype {

  /** a type key map of [[longevity.subdomain.persistent.Persistent
   * Persistent]] to [[PType]]
   * @see emblem.TypeKeyMap
   */
  type PTypePool = TypeKeyMap[Persistent, PType]

}
