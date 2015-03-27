package longevity.persistence

import longevity.context.LongevityContext

/** the persistence portion of the [[http://martinfowler.com/bliki/BoundedContext.html bounded context]]
 * for your [[http://bit.ly/1BPZfIW subdomain]]. the bounded context is a capture of the strategies and tools
 * used by the applications relating to your subdomain. in other words, those tools that speak the language of
 * the subdomain.
 *
 * @param persistenceStrategy the persistence strategy used by this context
 * @param specializations a collection factories for specialized repositories
 */
class PersistenceContext private[longevity] (
  val persistenceStrategy: PersistenceStrategy,
  val specializations: SpecializedRepoFactoryPool,
  longevityContext: LongevityContext) {

  /** the repositories for this persistence context, indexed by entity type */
  lazy val repoPool = repoPoolForLongevityContext(longevityContext)

}
