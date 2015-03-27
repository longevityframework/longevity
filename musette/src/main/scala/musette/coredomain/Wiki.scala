package musette.coredomain

import emblem._
import longevity.subdomain._

/** a wiki. */
case class Wiki(
  uri: Uri,
  site: Assoc[Site],
  authors: Set[Assoc[User]],
  slug: Markdown
)
extends SiteSection with RootEntity

object WikiType extends RootEntityType[Wiki]
