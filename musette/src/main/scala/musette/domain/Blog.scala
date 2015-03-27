package musette.domain

import emblem._
import longevity.subdomain._

/** a blog. */
case class Blog(
  uri: Uri,
  site: Assoc[Site],
  authors: Set[Assoc[User]],
  slug: Markdown
)
extends SiteSection with RootEntity

object BlogType extends RootEntityType[Blog]
