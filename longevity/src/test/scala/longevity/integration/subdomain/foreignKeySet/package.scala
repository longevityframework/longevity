package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a persistent with a set of associations to another persistent */
package object foreignKeySet {

  val subdomain = Subdomain("Foreign Key Set", PTypePool(WithForeignKeySet, Associated))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
