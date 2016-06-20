package longevity.subdomain

import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.ptype.PTypePool

/** a generic subdomain. functionally equivalent to a [[Subdomain]]
 *
 * @param name the name of the subdomain
 * @param pTypePool a complete set of the persistent types in the subdomain.
 * defaults to empty
 * @param entityTypePool a complete set of the entity types within the
 * subdomain. defaults to empty
 * @param shorthandPool a complete set of the shorthands used by the bounded
 * context. defaults to empty
 */
class GenericSubdomain(
  name: String,
  pTypePool: PTypePool = PTypePool.empty,
  entityTypePool: ETypePool = ETypePool.empty,
  shorthandPool: ShorthandPool = ShorthandPool.empty)
extends Subdomain(name, pTypePool, entityTypePool, shorthandPool)

object GenericSubdomain {

  /** constructs a generic subdomain. really just another name for a [[Subdomain]].
   *
   * @param name the name of the core domain
   * @param pTypePool a complete set of the persistent types in the subdomain.
   * defaults to empty
   * @param entityTypePool a complete set of the entity types within the core
   * domain. defaults to empty
   * @param shorthandPool a complete set of the shorthands used by the bounded
   * context. defaults to empty
   */
  def apply(
    name: String,
    pTypePool: PTypePool = PTypePool.empty,
    entityTypePool: ETypePool = ETypePool.empty,
    shorthandPool: ShorthandPool = ShorthandPool.empty)
  : GenericSubdomain = 
    new GenericSubdomain(name, pTypePool, entityTypePool, shorthandPool)

}
