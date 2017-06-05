package longevity.exceptions.persistence

import longevity.model.ptype.Key

/** an attempt to create or update a persistent object has failed because the
 * underlying database has detected a uniqueness violation for a key.
 */
class DuplicateKeyValException[M, P](val p: P, val key: Key[M, P, _], cause: Exception)
extends PersistenceException(
  s"attempt to persist $p has failed due to violation of uniqueness of key $key",
  cause) {

  def this(p: P, key: Key[M, P, _]) { this(p, key, null) }

}
