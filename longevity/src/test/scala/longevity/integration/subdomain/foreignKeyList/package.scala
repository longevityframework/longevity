package longevity.integration.subdomain

import longevity.TestLongevityConfigs
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool

/** covers a root entity with a list of associations to another root entity */
package object foreignKeyList {

  val subdomain = Subdomain("Foreign Key List", PTypePool(WithForeignKeyList, Associated))

  val contexts = TestLongevityConfigs.sparseContextMatrix(subdomain)

}
