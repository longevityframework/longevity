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
    import longevity.model.DomainModel
    import longevity.model.PTypePool

    val bloggingDomain = DomainModel(PTypePool.empty)
    val bloggingConfig: Config = loadBloggingConfig()
    val bloggingContext = LongevityContext(
      bloggingDomain,
      bloggingConfig)

    val accountsDomainModel = DomainModel(PTypePool.empty)
    val accountsConfig: Config = loadAccountsConfig()
    val accountsContext = LongevityContext(
      accountsDomainModel,
      accountsConfig)

    import com.typesafe.config.ConfigFactory
    def loadBloggingConfig(): Config = ConfigFactory.load()
    def loadAccountsConfig(): Config = ConfigFactory.load()
  }

  object config2 {

    import longevity.model.DomainModel
    import longevity.model.PTypePool

    val bloggingDomain = DomainModel(PTypePool.empty)

    import longevity.config.CassandraConfig
    import longevity.config.InMem
    import longevity.config.LongevityConfig
    import longevity.config.MongoDBConfig
    import longevity.config.JdbcConfig
    import longevity.config.TestConfig

    val longevityConfig = LongevityConfig(
      backEnd = InMem,
      autocreateSchema = false,
      optimisticLocking = false,
      writeTimestamps = false,
      cassandra = CassandraConfig(
        address = "localhost",
        credentials = None,
        keyspace = "longevity_main",
        replicationFactor = 1),
      mongodb = MongoDBConfig(
        uri = "localhost:27017",
        db = "longevity_main"),
      jdbc = JdbcConfig(
        driverClass = "org.sqlite.JDBC",
        url = "jdbc:sqlite:longevity_main.db"),
      test = TestConfig(
        cassandra = CassandraConfig(
          address = "localhost",
          credentials = None,
          keyspace = "longevity_test",
          replicationFactor = 1),
        mongodb = MongoDBConfig(
          uri = "localhost:27017",
          db = "longevity_test"),
        jdbc = JdbcConfig(
          driverClass = "org.sqlite.JDBC",
          url = "jdbc:sqlite:longevity_test.db")))

    import longevity.context.LongevityContext

    val bloggingContext = new LongevityContext(
      bloggingDomain,
      longevityConfig)
  }

}

/** exercises code samples found in the context section of the user manual.
 * the samples themselves are in [[ContextSpec]] companion object. we include
 * them in the tests here to force the initialization of the domain models, and
 * to perform some basic sanity checks on the results.
 *
 * @see http://longevityframework.github.io/longevity/manual/context
 */
class ContextSpec extends FlatSpec with GivenWhenThen with Matchers {

}
