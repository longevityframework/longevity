package musette.domain

import longevity.domain._

object WikiPage extends EntityType[WikiPage]

/** content authored by a site user. */
case class WikiPage(
  uri: Uri,
  author: Assoc[User],
  wiki: Assoc[Wiki],
  content: Markdown,
  slug: Markdown
)
extends TopContent with Entity
