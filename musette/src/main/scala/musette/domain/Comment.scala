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

  private lazy val uriProp =
    new EmblemProp[Comment, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri))
  private lazy val subjectProp =
      new EmblemProp[Comment, Assoc[BlogPost]]("subject", _.subject, (p, subject) => p.copy(subject = subject))
  private lazy val authorProp =
        new EmblemProp[Comment, Assoc[User]]("author", _.author, (p, author) => p.copy(author = author))
  private lazy val contentProp =
          new EmblemProp[Comment, Markdown]("content", _.content, (p, content) => p.copy(content = content))

  lazy val emblem = new Emblem[Comment](
    "musette.domain",
    "Comment",
    Seq(uriProp, subjectProp, authorProp, contentProp),
    EmblemPropToValueMap(),
    { map =>
      Comment(
        map.get(uriProp),
        map.get(subjectProp),
        map.get(authorProp),
        map.get(contentProp))
    }
  )

}
