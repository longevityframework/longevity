package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with an optional association to another persistent */
package object foreignKeyOption {

  val subdomain = Subdomain("Foreign Key Option", PTypePool(WithForeignKeyOption, Associated))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
