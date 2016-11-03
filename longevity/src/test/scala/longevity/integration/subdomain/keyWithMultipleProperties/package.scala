package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a persistent with a key that contains multiple properties */
package object keyWithMultipleProperties {

  val subdomain = Subdomain(
    "Key With Multiple Properties",
    PTypePool(KeyWithMultipleProperties),
    ETypePool(EType[Uri]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
