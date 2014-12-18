package musette.domain

import longevity.domain._

/** content authored by a site user. */
case class WikiPage(
  uri: Uri,
  authors: Set[Assoc[User]],
  wiki: Assoc[Wiki],
  content: Markdown,
  slug: Markdown
)
extends TopContent with Entity

object WikiPage extends EntityType[WikiPage]
