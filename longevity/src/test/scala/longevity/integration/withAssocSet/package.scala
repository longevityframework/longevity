package longevity.integration

import emblem._
import longevity.context._
import longevity.persistence._

import longevity.subdomain._

/** covers a root entity with an set of associations to another root entity */
package object withAssocSet {

  val entityTypes = EntityTypePool() + WithAssocSet + Associated

  val subdomain = Subdomain("With Assoc Set", entityTypes)

  val longevityContext = LongevityContext(subdomain, ShorthandPool(), Mongo)

}
