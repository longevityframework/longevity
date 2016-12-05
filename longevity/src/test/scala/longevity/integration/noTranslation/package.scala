package longevity.integration

import longevity.TestLongevityConfigs
import longevity.context.LongevityContext
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** a malformed subdomain that manages to include objects that don't have mongo transations. */
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
