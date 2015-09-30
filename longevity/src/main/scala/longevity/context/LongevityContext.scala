package longevity.context

import emblem.traversors.sync.Generator.CustomGeneratorPool
import longevity.subdomain._

object LongevityContext {

  /** constructs and returns a `LongevityContext`
   * 
   * @param subdomain the subdomain
   * @param persistenceStrategy the persistence strategy for this longevity context. defaults to [[Mongo]]
   * @param customGeneratorPool a collection of custom generators to use when generating test data. defaults to
   * empty
   */
  def apply(
    subdomain: Subdomain,
    persistenceStrategy: PersistenceStrategy = Mongo,
    customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty)
  : LongevityContext =
    new LongevityContextImpl(
      subdomain,
      persistenceStrategy,
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

}
