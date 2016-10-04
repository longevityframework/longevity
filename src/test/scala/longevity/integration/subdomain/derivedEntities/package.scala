package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.embeddable.DerivedEType
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.PolyEType
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a poly type and multiple derived types */
package object derived {

  val subdomain = Subdomain(
    "Derived",
    PTypePool(PolyRoot, FirstDerivedRoot, SecondDerivedRoot),
    ETypePool(
      PolyEType[PolyEmbeddable],
      DerivedEType[FirstDerivedEmbeddable, PolyEmbeddable],
      DerivedEType[SecondDerivedEmbeddable, PolyEmbeddable]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
