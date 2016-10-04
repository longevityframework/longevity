package longevity.ddd.subdomain

import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.PTypePool

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

/** provides a factory method for constructing [[CoreDomain core domains]] */
object CoreDomain {

  /** constructs a core domain. functionally equivalent to a [[Subdomain]].
   *
   * @param name the name of the core domain
   * @param pTypePool a complete set of the persistent types in the subdomain.
   * defaults to empty
   * @param entityTypePool a complete set of the entity types within the core
   * domain. defaults to empty
   * 
   * @see [[Subdomain.apply]] for a complete rundown of exceptions thrown on subdomain creation
   */
  def apply(
    name: String,
    pTypePool: PTypePool = PTypePool.empty,
    entityTypePool: ETypePool = ETypePool.empty)
  : CoreDomain = 
    new CoreDomain(name, pTypePool, entityTypePool)

}
