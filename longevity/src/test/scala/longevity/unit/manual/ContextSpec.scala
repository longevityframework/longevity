package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** code samples found in the context section of the user manual
 *
 * @see http://longevityframework.github.io/longevity/manual/context
 */
object ContextSpec {

  object config1 {

    import com.typesafe.config.Config
    import longevity.context.LongevityContext
    import longevity.subdomain.Subdomain
    import longevity.subdomain.PTypePool

    val bloggingDomain = Subdomain(PTypePool.empty)
    val bloggingConfig: Config = loadBloggingConfig()
    val bloggingContext = LongevityContext(
      bloggingDomain,
      bloggingConfig)

    val accountsSubdomain = Subdomain(PTypePool.empty)
    val accountsConfig: Config = loadAccountsConfig()
    val accountsContext = LongevityContext(
      accountsSubdomain,
      accountsConfig)

    import com.typesafe.config.ConfigFactory
    def loadBloggingConfig(): Config = ConfigFactory.load()
    def loadAccountsConfig(): Config = ConfigFactory.load()
  }

  object config2 {

    import longevity.subdomain.Subdomain
    import longevity.subdomain.PTypePool

    val bloggingDomain = Subdomain(PTypePool.empty)

    import longevity.context.CassandraConfig
    import longevity.context.InMem
    import longevity.context.LongevityConfig
    import longevity.context.MongoConfig
    import longevity.context.TestConfig

    val longevityConfig = LongevityConfig(
      backEnd = InMem,
      autocreateSchema = false,
      optimisticLocking = true,
      mongodb = MongoConfig(
        uri = "localhost:27017",
        db = "longevity_main"),
      cassandra = CassandraConfig(
        address = "localhost",
        credentials = None,
        keyspace = "longevity_main",
        replicationFactor = 1),
      test = TestConfig(
        mongodb = MongoConfig(
          uri = "localhost:27017",
          db = "longevity_test"),
        cassandra = CassandraConfig(
          address = "localhost",
          credentials = None,
          keyspace = "longevity_test",
          replicationFactor = 1)))

    import longevity.context.LongevityContext

    val bloggingContext = new LongevityContext(
      bloggingDomain,
      longevityConfig)
  }

}

/** exercises code samples found in the context section of the user manual.
 * the samples themselves are in [[ContextSpec]] companion object. we include
 * them in the tests here to force the initialization of the subdomains, and
 * to perform some basic sanity checks on the results.
 *
 * @see http://longevityframework.github.io/longevity/manual/context
 */
class ContextSpec extends FlatSpec with GivenWhenThen with Matchers {

}
