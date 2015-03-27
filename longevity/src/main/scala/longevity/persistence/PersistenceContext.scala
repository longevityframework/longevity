package longevity.persistence

import longevity.subdomain.Subdomain
import emblem.ShorthandPool

/** the persistence portion of the [[http://martinfowler.com/bliki/BoundedContext.html bounded context]]
 * for your [[http://bit.ly/1BPZfIW subdomain]]. the bounded context is a capture of the strategies and tools
 * used by the applications relating to your subdomain. in other words, those tools that speak the language of
 * the subdomain.
 *
 * @param subdomain the subdomain
 * @param shorthandPool a complete set of the shorthands used by the bounded context
 * @param persistenceStrategy the persistence strategy used by this context
 * @param specializations a collection factories for specialized repositories
 */
class PersistenceContext private[longevity] (
  subdomain: Subdomain,
  shorthandPool: ShorthandPool,
  val persistenceStrategy: PersistenceStrategy,
  val specializations: SpecializedRepoFactoryPool) {

  /** a pool of the repositories for this persistence context */
  lazy val repoPool = buildRepoPool(subdomain, shorthandPool, persistenceStrategy, specializations)

}
