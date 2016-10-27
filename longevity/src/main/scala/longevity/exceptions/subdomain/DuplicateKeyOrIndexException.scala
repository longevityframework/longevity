package longevity.exceptions.subdomain

import emblem.TypeKey

/** thrown on attempt to construct a
 * [[longevity.subdomain.Subdomain Subdomain]] with a
 * [[longevity.subdomain.PType PType]] that has two or more keys or indexes
 * defined over the same properties.
 */
class DuplicateKeyOrIndexException(
  val pTypeKey: TypeKey[_])
extends SubdomainException(
  s"PType ${pTypeKey.name} has duplicate keys or indexes")
