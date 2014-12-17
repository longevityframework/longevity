package musette.domain

import longevity.domain._

object BlogPost extends EntityType[BlogPost]

/** content authored by a site user. */
case class BlogPost(
  uri: Uri,
  author: Assoc[User],
  blog: Assoc[Blog],
  content: Markdown,
  slug: Markdown
)
extends TopContent with Entity
