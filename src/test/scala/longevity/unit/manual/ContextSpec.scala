package longevity.unit.manual

import org.scalatest._

/** code samples found in the context section of the user manual
 *
 * @see http://longevityframework.github.io/longevity/manual/context
 */
object ContextSpec {

  object config {

    import com.typesafe.config.Config
    import longevity.context.Cassandra
    import longevity.context.LongevityContext
    import longevity.context.Mongo
    import longevity.subdomain.CoreDomain
    import longevity.subdomain.SupportingSubdomain
    import longevity.subdomain.ptype.PTypePool

    val bloggingDomain: CoreDomain =
      CoreDomain("blogging", PTypePool.empty)
    val bloggingConfig: Config = loadBloggingConfig()
    val bloggingContext = LongevityContext(
      bloggingDomain,
      Mongo,
      config = bloggingConfig)

    val accountsSubdomain: SupportingSubdomain =
      SupportingSubdomain("accounts", PTypePool.empty)
    val accountsConfig: Config = loadAccountsConfig()
    val accountsContext = LongevityContext(
      accountsSubdomain,
      Cassandra,
      config = accountsConfig)

    import com.typesafe.config.ConfigFactory
    def loadBloggingConfig(): Config = ConfigFactory.empty()
    def loadAccountsConfig(): Config = ConfigFactory.empty()
  }

}

/** exercises code samples found in the context section of the user manual. the samples themselves are
 * in [[ContextSpec]] companion object. we include them in the tests here to force the initialization of the
 * subdomains, and to perform some basic sanity checks on the results.
 *
 * @see http://longevityframework.github.io/longevity/manual/context
 */
class ContextSpec extends FlatSpec with GivenWhenThen with Matchers {

  import ContextSpec._

  "user manual example code" should "compile" in {
    config.bloggingContext.subdomain.name should equal ("blogging")
    config.accountsContext.subdomain.name should equal ("accounts")
  }

}
