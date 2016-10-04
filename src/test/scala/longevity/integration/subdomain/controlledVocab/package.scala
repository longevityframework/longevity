package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.DerivedEType
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.PolyEType
import longevity.subdomain.ptype.PTypePool

/** covers a controlled vocab created with a poly type and multiple derived case objects */
package object controlledVocab {

  val subdomain = Subdomain(
    "Controlled Vocabulary",
    PTypePool(WithControlledVocab),
    ETypePool(
      PolyEType[ControlledVocab],
      DerivedEType[VocabTerm1, ControlledVocab],
      DerivedEType[VocabTerm2.type, ControlledVocab]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
