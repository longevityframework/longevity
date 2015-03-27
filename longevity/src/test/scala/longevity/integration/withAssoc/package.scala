package longevity.integration

import emblem._
import longevity.context._
import longevity.subdomain._

/** covers a root entity with a single association to another root entity */
package object withAssoc {

  val entityTypes = EntityTypePool() + WithAssoc + Associated

  val subdomain = Subdomain("With Assoc", entityTypes)

  val longevityContext = LongevityContext(Mongo, subdomain)

}
