package longevity.integration

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.model.ModelType
import longevity.model.PTypePool

/** a malformed domain model that manages to include types outside the model */
package object noTranslation {

  val pTypes = PTypePool(
    WithNoTranslation,
    WithNoTranslationList,
    WithNoTranslationLonghand,
    WithNoTranslationOption,
    WithNoTranslationSet)

  val domainModel = ModelType(pTypes)
  val cassandraContext = new LongevityContext(domainModel, TestLongevityConfigs.cassandraConfig)
  val inMemContext     = new LongevityContext(domainModel, TestLongevityConfigs.inMemConfig)
  val mongoContext     = new LongevityContext(domainModel, TestLongevityConfigs.mongoConfig)
  val sqliteContext    = new LongevityContext(domainModel, TestLongevityConfigs.sqliteConfig)

}
