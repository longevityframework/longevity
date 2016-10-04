package longevity.ddd.subdomain

import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.ptype.PTypePool

/** a generic subdomain. functionally equivalent to a [[Subdomain]].
 *
 * @param name the name of the subdomain
 * @param pTypePool a complete set of the persistent types in the subdomain.
 * defaults to empty
 * @param entityTypePool a complete set of the entity types within the
 * subdomain. defaults to empty
 */
class GenericSubdomain(
  name: String,
  pTypePool: PTypePool = PTypePool.empty,
  entityTypePool: ETypePool = ETypePool.empty)
extends Subdomain(name, pTypePool, entityTypePool)

/** provides a factory method for constructing [[GenericSubdomain generic subdomains]] */
object GenericSubdomain {

  /** constructs a generic subdomain. functionally equivalent to a [[Subdomain]].
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
  : GenericSubdomain = 
    new GenericSubdomain(name, pTypePool, entityTypePool)

}
