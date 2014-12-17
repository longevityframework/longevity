package musette.domain

import longevity.domain._

/** a wiki. */
case class Wiki(
  uri: Uri,
  authors: Set[Assoc[User]],
  slug: Markdown
)
extends SiteSection with Entity
