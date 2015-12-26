package longevity.exceptions.subdomain

import longevity.subdomain._

/** thrown on attempt to use an unpersisted assoc as a persisted assoc. for instance,
 * when calling [[Assoc.retrieve]], or attempting to persist with [[Repo.create]]
 */
class AssocIsUnpersistedException(assoc: Assoc[_ <: Root])
extends AssocException(assoc, "cannot retrieve a persisted aggregate from an unpersisted assoc")
