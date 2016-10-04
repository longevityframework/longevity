package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with a single association to another root entity */
package object foreignKey {

  val subdomain = Subdomain("Foreign Key", PTypePool(WithForeignKey, Associated))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
