package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.DerivedCType
import longevity.subdomain.CTypePool
import longevity.subdomain.PolyCType
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with a poly type and multiple derived types */
package object derived {

  val subdomain = Subdomain(
    "Derived",
    PTypePool(PolyPersistent, FirstDerivedPersistent, SecondDerivedPersistent),
    CTypePool(
      PolyCType[PolyEmbeddable],
      DerivedCType[FirstDerivedEmbeddable, PolyEmbeddable],
      DerivedCType[SecondDerivedEmbeddable, PolyEmbeddable]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
