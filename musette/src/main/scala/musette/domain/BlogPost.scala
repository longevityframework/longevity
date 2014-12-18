package musette.domain

import longevity.domain._

/** content authored by a site user. */
case class BlogPost(
  uri: Uri,
  authors: Set[Assoc[User]],
  blog: Assoc[Blog],
  content: Markdown,
  slug: Markdown
)
extends TopContent with Entity

object BlogPost extends EntityType[BlogPost]
