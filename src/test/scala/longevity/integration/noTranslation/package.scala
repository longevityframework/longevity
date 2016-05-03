package longevity.integration

import longevity.context.LongevityContext
import longevity.context.Cassandra
import longevity.context.Mongo
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

// TODO: do i need cassandra/inmem versions here?

/** a malformed subdomain that manages to include objects that don't have mongo transations. */
package object noTranslation {

  implicit val shorthandPool = ShorthandPool.empty

  object context {

    val pTypes = PTypePool(
      WithNoTranslation,
      WithNoTranslationList,
      WithNoTranslationLonghand,
      WithNoTranslationOption,
      WithNoTranslationSet)

    val subdomain = Subdomain("No Translation", pTypes)
    val longevityContext = LongevityContext(subdomain, Mongo)
  }

}
