package musette.domain

import emblem._
import longevity.domain._

case class Comment(
  uri: Uri,
  subject: Assoc[BlogPost],
  author: Assoc[User],
  content: Markdown
)
extends Content with Entity {
  override val authors = Set(author)
}

object CommentType extends EntityType[Comment]
