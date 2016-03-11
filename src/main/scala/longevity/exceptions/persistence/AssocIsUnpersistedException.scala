package longevity.exceptions.persistence

import longevity.subdomain.Assoc
import longevity.subdomain.Persistent

/** thrown on attempt to use an unpersisted assoc as a persisted assoc. for instance,
 * when calling [[longevity.persistence.Repo repository method]] `retrieve(Assoc[P])`,
 * or attempting to persist with [[longevity.persistence.Repo.create]]
 */
class AssocIsUnpersistedException(val assoc: Assoc[_ <: Persistent])
extends PersistenceException(
  "cannot retrieve a persisted aggregate from an unpersisted assoc")
