package longevity.integration

import emblem._
import longevity.context._
import longevity.persistence._
import longevity.subdomain._

/** covers a root entity with attributes of every supported basic type */
package object allAttributes {

  val entityTypes = EntityTypePool() + AllAttributes

  val subdomain = Subdomain("All Attributes", entityTypes)

  val longevityContext = LongevityContext(Mongo, subdomain)

}
