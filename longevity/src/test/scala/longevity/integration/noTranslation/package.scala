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

  val modelType = ModelType(pTypes)
  val cassandraContext = new LongevityContext(modelType, TestLongevityConfigs.cassandraConfig)
  val inMemContext     = new LongevityContext(modelType, TestLongevityConfigs.inMemConfig)
  val mongoContext     = new LongevityContext(modelType, TestLongevityConfigs.mongoConfig)
  val sqliteContext    = new LongevityContext(modelType, TestLongevityConfigs.sqliteConfig)

}
