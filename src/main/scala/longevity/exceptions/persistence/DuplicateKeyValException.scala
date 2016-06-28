package longevity.exceptions.persistence

import longevity.subdomain.persistent.Persistent
import longevity.subdomain.ptype.AnyKey

/** an attempt to create or update a persistent object has failed because the
 * underlying database has detected a uniqueness violation for a key.
 */
class DuplicateKeyValException[P <: Persistent](
  val p: P,
  val key: AnyKey[P],
  cause: Exception)
extends PersistenceException(
  s"attempt to persist $p has failed due to violation of uniqueness of key $key",
  cause) {

  def this(p: P, key: AnyKey[P]) { this(p, key, null) }

}
