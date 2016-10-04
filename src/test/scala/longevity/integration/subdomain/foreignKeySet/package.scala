package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with a set of associations to another root entity */
package object foreignKeySet {

  val subdomain = Subdomain("Foreign Key Set", PTypePool(WithForeignKeySet, Associated))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
