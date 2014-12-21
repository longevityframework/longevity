package musette.domain

import longevity.domain._

/** content authored by a site user. */
case class Comment(
  uri: Uri,
  subject: Assoc[Content],
  author: Assoc[User],
  content: Markdown
)
extends Content with Entity {
  override val authors = Set(author)
}

object Comment extends EntityType[Comment] {

  override val assocLenses =
    lens1(_.subject)({ (e, assoc) => e.copy(subject = assoc) }) ::
    lens1(_.author)({ (e, assoc) => e.copy(author = assoc) }) ::
    Nil

}
