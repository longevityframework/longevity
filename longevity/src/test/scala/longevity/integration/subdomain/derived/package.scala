package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.CTypePool
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with a poly type and multiple derived types */
package object derived {

  val subdomain = Subdomain(
    "Derived",
    PTypePool(PolyPersistent, FirstDerivedPersistent, SecondDerivedPersistent),
    CTypePool(
      PolyEmbeddable,
      FirstDerivedEmbeddable,
      SecondDerivedEmbeddable))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
