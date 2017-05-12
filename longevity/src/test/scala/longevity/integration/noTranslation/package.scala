package longevity.integration

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.model.annotations.domainModel

/** a malformed domain model that manages to include types outside the model */
package object noTranslation {

  @domainModel trait DomainModel

  val cassandraContext = new LongevityContext[DomainModel](TestLongevityConfigs.cassandraConfig)
  val inMemContext     = new LongevityContext[DomainModel](TestLongevityConfigs.inMemConfig)
  val mongoContext     = new LongevityContext[DomainModel](TestLongevityConfigs.mongoConfig)
  val sqliteContext    = new LongevityContext[DomainModel](TestLongevityConfigs.sqliteConfig)

}
