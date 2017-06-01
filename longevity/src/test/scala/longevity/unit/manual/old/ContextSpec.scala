package longevity.unit.manual

/** code samples found in the context section of the user manual
 *
 * @see http://longevityframework.github.io/longevity/manual/context
 */
object ContextSpec {

  object config1 {

    import com.typesafe.config.Config
    import longevity.context.LongevityContext
    import longevity.model.ModelEv
    import longevity.model.ModelType

    trait BloggingDomain
    object BloggingDomain {
      implicit object modelType extends ModelType[BloggingDomain](Nil)
      private[config1] implicit object modelEv extends ModelEv[BloggingDomain]
    }
    val bloggingConfig: Config = loadBloggingConfig()
    val bloggingContext = LongevityContext[BloggingDomain](bloggingConfig)

    trait AccountsDomain
    object AccountsDomain {
      implicit object modelType extends ModelType[AccountsDomain](Nil)
      private[config1] implicit object modelEv extends ModelEv[AccountsDomain]
    }
    val accountsConfig: Config = loadAccountsConfig()
    val accountsContext = LongevityContext[AccountsDomain](accountsConfig)

    import com.typesafe.config.ConfigFactory
    def loadBloggingConfig(): Config = ConfigFactory.load()
    def loadAccountsConfig(): Config = ConfigFactory.load()
  }

  object config2 {

    import longevity.model.ModelType

    trait BloggingDomain
    object BloggingDomain {
      implicit object modelType extends ModelType[BloggingDomain](Nil)
    }

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

    val bloggingContext = new LongevityContext[BloggingDomain](longevityConfig)
  }

}
