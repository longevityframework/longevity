package longevity.exceptions.subdomain

import longevity.subdomain.Assoc
import longevity.subdomain.Root

// TODO this should be a persistence exception

/** thrown on attempt to use an unpersisted assoc as a persisted assoc. for instance,
 * when calling [[longevity.persistence.Repo.retrieve(Assoc)]], or attempting to persist with
 * [[longevity.persistence.Repo.create]]
 */
class AssocIsUnpersistedException(assoc: Assoc[_ <: Root])
extends AssocException(assoc, "cannot retrieve a persisted aggregate from an unpersisted assoc")
