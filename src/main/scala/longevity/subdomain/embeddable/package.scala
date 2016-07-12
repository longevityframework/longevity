package longevity.subdomain

import emblem.TypeKeyMap

/** provides tools for defining classes to embed within your
 * [[longevity.subdomain.persistent.Persistent persistent objects]]
 */
package object embeddable {

  /** a type key map of [[Embeddable]] to [[EType]]
   * @see emblem.TypeKeyMap
   */
  type ETypePool = TypeKeyMap[Embeddable, EType]

}
