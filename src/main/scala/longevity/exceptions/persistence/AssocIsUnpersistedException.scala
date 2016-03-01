package longevity.exceptions.persistence

import longevity.subdomain.Assoc
import longevity.subdomain.Root

/** thrown on attempt to use an unpersisted assoc as a persisted assoc. for instance,
 * when calling [[longevity.persistence.Repo repository method]] `retrieve(Assoc[R])`,
 * or attempting to persist with [[longevity.persistence.Repo.create]]
 */
class AssocIsUnpersistedException(val assoc: Assoc[_ <: Root])
extends PersistenceException(
  "cannot retrieve a persisted aggregate from an unpersisted assoc")
