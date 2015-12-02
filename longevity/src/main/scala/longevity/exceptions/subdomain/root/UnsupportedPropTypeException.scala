package longevity.exceptions.subdomain.root

import emblem.imports._
import longevity.subdomain.RootEntity

/** thrown on attempt to create a property with a type that longevity doesn't support yet. these include
 * and properties that are not exactly-one valued - no options, sets or lists anywhere in the path. these
 * also include paths that end on an entity, and not a basic type, shorthand or assoc.
 */
class UnsupportedPropTypeException[R <: RootEntity : TypeKey, U : TypeKey](val path: String)
extends PropException(
  s"longevity doesn't currently support properties with type `${typeKey[U].name}`, such as " +
  s"`$path` in `${typeKey[R]}")

