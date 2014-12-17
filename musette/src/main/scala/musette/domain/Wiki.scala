package musette.domain

import longevity.domain._

/** a wiki. */
case class Wiki(
  uri: Uri,
  site: Assoc[Site],
  authors: Set[Assoc[User]],
  slug: Markdown
)
extends SiteSection with Entity

object Wiki extends EntityType[Wiki]
