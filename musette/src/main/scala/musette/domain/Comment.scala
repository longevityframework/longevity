package musette.domain

import longevity.domain._

/** content authored by a site user. */
case class Comment(
  uri: Uri,
  author: Assoc[User],
  subject: Assoc[Content],
  content: Markdown
)
extends Content with Entity {
  override val authors = Set(author)
}

object Comment extends EntityType[Comment]
