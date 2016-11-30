package longevity.exceptions.subdomain

import emblem.TypeKey

/** thrown on attempt to construct a
 * [[longevity.subdomain.Subdomain Subdomain]] with a
 * [[longevity.subdomain.PType PType]] that has a key and an index
 * defined over the same properties
 */
class IndexDuplicatesKeyException(
  val pTypeKey: TypeKey[_])
extends SubdomainException(
  s"PType ${pTypeKey.name} has duplicate keys or indexes")
