package musette.domain

import emblem._
import longevity.domain._

/** content authored by a site user. */
case class WikiPage(
  uri: Uri,
  wiki: Assoc[Wiki],
  authors: Set[Assoc[User]],
  content: Markdown,
  slug: Markdown
)
extends TopContent with RootEntity

object WikiPageType extends RootEntityType[WikiPage]
