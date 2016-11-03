package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ETypePool
import longevity.subdomain.EType
import longevity.subdomain.PTypePool

/** covers a root entity with a key that contains a foreign key value */
package object keyWithForeignKey {

  val subdomain = Subdomain(
    "Key With Foreign Key",
    PTypePool(KeyWithForeignKey, Associated),
    ETypePool(EType[Uri]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
