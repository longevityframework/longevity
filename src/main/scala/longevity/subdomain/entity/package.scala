package longevity.subdomain

import emblem.TypeKeyMap

/** provides support for constructing your subdomain */
package object entity {

  /** a type key map of [[Entity]] to [[EntityType]]
   * @see emblem.TypeKeyMap
   */
  type EntityTypePool = TypeKeyMap[Entity, EntityType]

}
