package musette.coredomain

import emblem._
import longevity.subdomain._

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
