package longevity.context

import emblem.traversors.Generator.CustomGeneratorPool
import longevity.persistence.InMem
import longevity.persistence.PersistenceContext
import longevity.persistence.PersistenceStrategy
import longevity.persistence.SpecializedRepoFactoryPool
import longevity.subdomain.Subdomain
import longevity.test.TestContext

/** contains factory methods and implicits for longevity contexts */
object LongevityContext {

  /** constructs and returns a `LongevityContext`
   * 
   * @param subdomain the subdomain
   * @param shorthandPool a complete set of the shorthands used by the bounded context
   * @param persistenceStrategy the persistence strategy for this longevity context
   * @param specializations a collection factories for specialized repositories
   * @param customGenerators a collection of custom generators to use when generating test data. defaults to an
   * empty collection.
   */
  def apply(
    subdomain: Subdomain,
    shorthandPool: ShorthandPool,
    persistenceStrategy: PersistenceStrategy,
    specializations: SpecializedRepoFactoryPool = SpecializedRepoFactoryPool.empty,
    customGenerators: CustomGeneratorPool = CustomGeneratorPool.empty)
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
  val customGenerators: CustomGeneratorPool) {

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
