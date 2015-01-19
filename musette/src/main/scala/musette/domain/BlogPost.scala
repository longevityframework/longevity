package musette.domain

import emblem._
import longevity.domain._

/** content authored by a site user. */
case class BlogPost(
  uri: Uri,
  blog: Assoc[Blog],
  authors: Set[Assoc[User]],
  content: Markdown,
  slug: Markdown
)
extends TopContent with Entity

object BlogPostType extends EntityType[BlogPost]
