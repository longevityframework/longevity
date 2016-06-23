package longevity.subdomain

import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.ptype.PTypePool

/** a core domain. functionally equivalent to a [[Subdomain]]
 *
 * @param name the name of the subdomain
 * @param pTypePool a complete set of the persistent types in the subdomain.
 * defaults to empty
 * @param entityTypePool a complete set of the entity types within the
 * subdomain. defaults to empty
 */
class CoreDomain(
  name: String,
  pTypePool: PTypePool = PTypePool.empty,
  entityTypePool: ETypePool = ETypePool.empty)
extends Subdomain(name, pTypePool, entityTypePool)

object CoreDomain {

  /** constructs a core domain. really just another name for a [[Subdomain]].
   *
   * @param name the name of the core domain
   * @param pTypePool a complete set of the persistent types in the subdomain.
   * defaults to empty
   * @param entityTypePool a complete set of the entity types within the core
   * domain. defaults to empty
   */
  def apply(
    name: String,
    pTypePool: PTypePool = PTypePool.empty,
    entityTypePool: ETypePool = ETypePool.empty)
  : CoreDomain = 
    new CoreDomain(name, pTypePool, entityTypePool)

}
