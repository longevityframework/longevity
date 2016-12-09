package longevity.integration

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.model.DomainModel
import longevity.model.PTypePool

/** a malformed domain model that manages to include types outside the model */
package object noTranslation {

  val pTypes = PTypePool(
    WithNoTranslation,
    WithNoTranslationList,
    WithNoTranslationLonghand,
    WithNoTranslationOption,
    WithNoTranslationSet)

  val domainModel = DomainModel(pTypes)
  val inMemContext = new LongevityContext(domainModel, TestLongevityConfigs.inMemConfig)
  val mongoContext = new LongevityContext(domainModel, TestLongevityConfigs.mongoConfig)
  val cassandraContext = new LongevityContext(domainModel, TestLongevityConfigs.cassandraConfig)

}
