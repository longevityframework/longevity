package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.DerivedType
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.PolyType
import longevity.subdomain.ptype.PTypePool

/** covers a controlled vocab created with a poly type and multiple derived case objects */
package object controlledVocab {

  object context {
    val subdomain = Subdomain(
      "Controlled Vocabulary",
      PTypePool(WithControlledVocab),
      ETypePool(
        PolyType[ControlledVocab],
        DerivedType[VocabTerm1, ControlledVocab],
        DerivedType[VocabTerm2.type, ControlledVocab]))
    val mongoContext = LongevityContext(subdomain, Mongo)
    val cassandraContext = LongevityContext(subdomain, Cassandra)
  }

}
