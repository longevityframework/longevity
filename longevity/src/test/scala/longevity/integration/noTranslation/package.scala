package longevity.integration

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.model.annotations.domainModel
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** a malformed domain model that manages to include types outside the model */
package object noTranslation {

  @domainModel trait DomainModel

  val cassandraContext = new LongevityContext[Future, DomainModel](TestLongevityConfigs.cassandraConfig)
  val inMemContext     = new LongevityContext[Future, DomainModel](TestLongevityConfigs.inMemConfig)
  val mongoContext     = new LongevityContext[Future, DomainModel](TestLongevityConfigs.mongoConfig)
  val sqliteContext    = new LongevityContext[Future, DomainModel](TestLongevityConfigs.sqliteConfig)

}
