package musette.domain

import longevity.domain._

/** a blog. */
case class Blog(
  uri: Uri,
  authors: Set[Assoc[User]],
  slug: Markdown
)
extends SiteSection with Entity
