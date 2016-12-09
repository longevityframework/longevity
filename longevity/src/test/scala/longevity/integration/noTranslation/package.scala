package longevity.integration

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.model.Subdomain
import longevity.model.PTypePool

/** a malformed domain model that manages to include types outside the model */
package object noTranslation {

  val pTypes = PTypePool(
    WithNoTranslation,
    WithNoTranslationList,
    WithNoTranslationLonghand,
    WithNoTranslationOption,
    WithNoTranslationSet)

  val subdomain = Subdomain(pTypes)
  val inMemContext = new LongevityContext(subdomain, TestLongevityConfigs.inMemConfig)
  val mongoContext = new LongevityContext(subdomain, TestLongevityConfigs.mongoConfig)
  val cassandraContext = new LongevityContext(subdomain, TestLongevityConfigs.cassandraConfig)

}
