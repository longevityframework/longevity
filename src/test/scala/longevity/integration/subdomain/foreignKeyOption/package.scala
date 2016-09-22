package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with an optional association to another root entity */
package object foreignKeyOption {

  val subdomain = Subdomain("Foreign Key Option", PTypePool(WithForeignKeyOption, Associated))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
