package musette.domain

import emblem._
import longevity.domain._

/** a wiki. */
case class Wiki(
  uri: Uri,
  site: Assoc[Site],
  authors: Set[Assoc[User]],
  slug: Markdown
)
extends SiteSection with Entity

object WikiType extends EntityType[Wiki]
