package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.DerivedEType
import longevity.subdomain.ETypePool
import longevity.subdomain.PolyEType
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with a poly type and multiple derived types */
package object derived {

  val subdomain = Subdomain(
    "Derived",
    PTypePool(PolyPersistent, FirstDerivedPersistent, SecondDerivedPersistent),
    ETypePool(
      PolyEType[PolyEmbeddable],
      DerivedEType[FirstDerivedEmbeddable, PolyEmbeddable],
      DerivedEType[SecondDerivedEmbeddable, PolyEmbeddable]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
