package longevity.exceptions.subdomain

import longevity.subdomain.Assoc
import longevity.subdomain.Root

/** thrown on attempt to use an unpersisted assoc as a persisted assoc. for instance,
 * when calling [[longevity.subdomain.Assoc.retrieve]], or attempting to persist with
 * [[longevity.persistence.Repo.create]]
 */
class AssocIsUnpersistedException(assoc: Assoc[_ <: Root])
extends AssocException(assoc, "cannot retrieve a persisted aggregate from an unpersisted assoc")
