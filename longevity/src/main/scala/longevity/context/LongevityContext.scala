package longevity.context

import emblem.traversors.Generator.CustomGeneratorPool
import longevity.shorthands._
import longevity.subdomain.Subdomain

object LongevityContext {

  /** constructs and returns a `LongevityContext`
   * 
   * @param subdomain the subdomain
   * 
   * @param shorthandPool a complete set of the shorthands used by the bounded context. defaults to empty
   * 
   * @param persistenceStrategy the persistence strategy for this longevity context. defaults to [[InMem]]
   * 
   * @param specializedRepoFactoryPool a collection factories for specialized repositories. defaults to empty
   * 
   * @param customGeneratorPool a collection of custom generators to use when generating test data. defaults to
   * empty
   */
  def apply(
    subdomain: Subdomain,
    shorthandPool: ShorthandPool = ShorthandPool.empty,
    persistenceStrategy: PersistenceStrategy = InMem,
    specializedRepoFactoryPool: SpecializedRepoFactoryPool = SpecializedRepoFactoryPool.empty,
    customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty)
  : LongevityContext =
    new LongevityContextImpl(
      subdomain,
      shorthandPool,
      persistenceStrategy,
      specializedRepoFactoryPool,
      customGeneratorPool)

}

/** the longevity managed portion of the [[http://martinfowler.com/bliki/BoundedContext.html bounded context]]
 * for your [[http://bit.ly/1BPZfIW subdomain]]. the bounded context is a capture of the strategies and tools
 * used by the applications relating to your subdomain. in other words, those tools that speak the language of
 * the subdomain.
 */
trait LongevityContext extends PersistenceContext with TestContext {

  /** the subdomain that provides the ubiquitous language for the bounded context */
  val subdomain: Subdomain

  /** a complete set of the shorthands used by the bounded context */
  val shorthandPool: ShorthandPool

}
