package longevity.subdomain

import emblem.TypeKeyMap

/** provides support for embedding case classes within your [persistent
 * objects](longevity.subdomain.persistent)
 */
package object embeddable {

  /** a type key map of [[Entity]] to [[EntityType]]
   * @see emblem.TypeKeyMap
   */
  type EntityTypePool = TypeKeyMap[Entity, EntityType]

}
