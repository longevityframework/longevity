package longevity.context

import emblem.ShorthandPool
import emblem.traversors.Generator.CustomGenerators
import emblem.traversors.Generator.emptyCustomGenerators
import longevity.persistence.InMem
import longevity.persistence.PersistenceContext
import longevity.persistence.PersistenceStrategy
import longevity.persistence.SpecializedRepoFactoryPool
import longevity.persistence.emptySpecializedRepoFactoryPool
import longevity.subdomain.Subdomain
import longevity.test.TestContext

// TODO scaladoc in here
object LongevityContext {

  // TODO reorder params to match below
  def apply(
    persistenceStrategy: PersistenceStrategy,
    subdomain: Subdomain,
    shorthandPool: ShorthandPool = ShorthandPool(),
    specializations: SpecializedRepoFactoryPool = emptySpecializedRepoFactoryPool,
    customGenerators: CustomGenerators = emptyCustomGenerators)
  : LongevityContext = {
    new LongevityContext(
      subdomain,
      shorthandPool,
      persistenceStrategy,
      specializations,
      customGenerators)
  }

  implicit def longevityContextSubdomain(longevityContext: LongevityContext): Subdomain =
    longevityContext.subdomain

  implicit def longevityContextToPersistenceContext(longevityContext: LongevityContext): PersistenceContext =
    longevityContext.persistenceContext

  implicit def longevityContextToTestContext(longevityContext: LongevityContext): TestContext =
    longevityContext.testContext

}

/** the longevity managed portion of the [[http://martinfowler.com/bliki/BoundedContext.html bounded context]]
 * for your [[http://bit.ly/1BPZfIW subdomain]]. the bounded context is a capture of the strategies and tools
 * used by the applications relating to your subdomain. in other words, those tools that speak the language of
 * the subdomain.
 *
 * @param subdomain the subdomain
 * @param shorthandPool a complete set of the shorthands used by the bounded context
 * @param persistenceStrategy the persistence strategy for this longevity context
 * @param specializations a collection factories for specialized repositories
 * @param customGenerators a collection of custom generators to use when generating test data. defaults to an
 * empty collection.
 */
final class LongevityContext private(
  val subdomain: Subdomain,
  val shorthandPool: ShorthandPool,
  persistenceStrategy: PersistenceStrategy,
  specializations: SpecializedRepoFactoryPool,
  val customGenerators: CustomGenerators) {

  lazy val persistenceContext = new PersistenceContext(
    subdomain,
    shorthandPool,
    persistenceStrategy,
    specializations)

  lazy val testContext = new TestContext(
    subdomain,
    shorthandPool,
    customGenerators,
    persistenceContext.repoPool)

}
