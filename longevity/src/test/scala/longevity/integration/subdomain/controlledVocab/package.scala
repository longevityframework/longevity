package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.DerivedCType
import longevity.subdomain.CTypePool
import longevity.subdomain.PolyCType
import longevity.subdomain.PTypePool

/** covers a controlled vocab created with a poly type and multiple derived case objects */
package object controlledVocab {

  val subdomain = Subdomain(
    "Controlled Vocabulary",
    PTypePool(WithControlledVocab),
    CTypePool(
      PolyCType[ControlledVocab],
      DerivedCType[VocabTerm1, ControlledVocab],
      DerivedCType[VocabTerm2.type, ControlledVocab]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
