package musette.domain

import emblem._
import longevity.domain._

/** a website. */
case class Site(
  val uri: Uri
)
extends Entity

object SiteType extends EntityType[Site]
