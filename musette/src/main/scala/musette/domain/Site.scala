package musette.domain

import longevity.domain._

/** a website. */
case class Site(
  val uri: Uri
)
extends Entity

object Site extends EntityType[Site]
