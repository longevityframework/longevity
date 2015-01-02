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

object Comment extends EntityType[Comment] {

  lazy val emblem = new Emblem[Comment](
    "musette.domain",
    "Comment",
    Seq(
      new EmblemProp[Comment, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri)),
      new EmblemProp[Comment, Assoc[BlogPost]]("subject", _.subject, (p, subject) => p.copy(subject = subject)),
      new EmblemProp[Comment, Assoc[User]]("author", _.author, (p, author) => p.copy(author = author)),
      new EmblemProp[Comment, Markdown]("content", _.content, (p, content) => p.copy(content = content))
    )
  )

}
