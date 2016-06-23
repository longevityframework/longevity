package longevity.integration

import longevity.context.LongevityContext
import longevity.context.Cassandra
import longevity.context.Mongo
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** a malformed subdomain that manages to include objects that don't have mongo transations. */
package object noTranslation {

  val pTypes = PTypePool(
    WithNoTranslation,
    WithNoTranslationList,
    WithNoTranslationLonghand,
    WithNoTranslationOption,
    WithNoTranslationSet)

  val subdomain = Subdomain("No Translation", pTypes)
  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}
