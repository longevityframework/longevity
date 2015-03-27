package longevity.context

import longevity.persistence.SpecializedRepoFactoryPool
import longevity.persistence.emptySpecializedRepoFactoryPool
import longevity.persistence.repoPoolForLongevityContext
import longevity.persistence.testRepoPoolForLongevityContext
import longevity.domain.Subdomain
import emblem.ShorthandPool
import emblem.traversors.Generator.CustomGenerators
import emblem.traversors.Generator.emptyCustomGenerators

/** the longevity managed portion of the [[http://martinfowler.com/bliki/BoundedContext.html bounded context]]
 * for your [[http://bit.ly/1BPZfIW subdomain]]. the bounded context is a capture of the strategies and tools
 * used by the applications relating to your subdomain. in other words, those tools that speak the language of
 * the subdomain.
 *
 * @tparam PS the kind of persistence strategy for this longevity context
 * @param subdomain The subdomain
 * @param shorthandPool a complete set of the shorthands used by the domain
 * @param specializations a collection factories for specialized repositories
 * @param customGenerators a collection of custom generators to use when generating test data. defaults to an
 * empty collection.
 * @param persistenceStrategy the persistence strategy for this longevity context
 */
case class LongevityContext(
  persistenceStrategy: PersistenceStrategy,
  subdomain: Subdomain,
  shorthandPool: ShorthandPool = ShorthandPool(),
  specializations: SpecializedRepoFactoryPool = emptySpecializedRepoFactoryPool,
  customGenerators: CustomGenerators = emptyCustomGenerators) {

  /** The standard set of repositories for this longevity context */
  lazy val repoPool = repoPoolForLongevityContext(this)

  /** An in-memory set of repositories for this longevity context, for use in testing. at the moment, no
   * specializations are provided. */
  lazy val inMemRepoPool = testRepoPoolForLongevityContext(this)

  /** a simple [[http://www.scalatest.org/ ScalaTest]] fixture to test your [[repoPool repo pool]].
   * all you have to do is extend this class some place where ScalaTest is going to find it.
   */
  class RepoPoolSpec extends longevity.persistence.testUtil.RepoPoolSpec(
    this,
    this.repoPool,
    suiteNameSuffix = Some("(Mongo)"))

  /** a simple [[http://www.scalatest.org/ ScalaTest]] fixture to test your [[inMemRepoPool in-memory repo
   * pool]]. all you have to do is extend this class some place where ScalaTest is going to find it.
   */
  class InMemRepoPoolSpec extends longevity.persistence.testUtil.RepoPoolSpec(
    this,
    inMemRepoPool,
    suiteNameSuffix = Some("(InMem)"))

}
