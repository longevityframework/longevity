package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.PTypePool

/** covers a controlled vocab created with a poly type and multiple derived case objects */
package object controlledVocab {

  val subdomain = Subdomain(
    "Controlled Vocabulary",
    PTypePool(WithControlledVocab),
    CTypePool(
      ControlledVocab,
      VocabTerm1,
      VocabTerm2.ctype))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
