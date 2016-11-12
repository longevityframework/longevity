package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.CTypePool
import longevity.subdomain.CType
import longevity.subdomain.PTypePool

/** covers a persistent with a key that contains a foreign key value */
package object keyWithForeignKey {

  val subdomain = Subdomain(
    "Key With Foreign Key",
    PTypePool(KeyWithForeignKey, Associated),
    CTypePool(CType[Uri]))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
