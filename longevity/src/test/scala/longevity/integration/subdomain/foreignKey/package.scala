package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with a single association to another persistent */
package object foreignKey {

  val subdomain = Subdomain("Foreign Key", PTypePool(WithForeignKey, Associated))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
