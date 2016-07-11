package longevity.integration.subdomain

import longevity.context.Cassandra
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.Subdomain
import longevity.subdomain.ptype.PTypePool

/** covers a root entity with a list of associations to another root entity */
package object foreignKeyList {

  val subdomain = Subdomain("Foreign Key List", PTypePool(WithForeignKeyList, Associated))
  val mongoContext = LongevityContext(subdomain, Mongo)
  val cassandraContext = LongevityContext(subdomain, Cassandra)

}
