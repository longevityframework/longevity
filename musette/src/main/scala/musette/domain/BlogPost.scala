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

object BlogPost extends EntityType[BlogPost] {

  lazy val emblem = new Emblem[BlogPost](
    "musette.domain",
    "BlogPost",
    Seq(
      new EmblemProp[BlogPost, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri)),
      new EmblemProp[BlogPost, Assoc[Blog]]("blog", _.blog, (p, blog) => p.copy(blog = blog)),
      new EmblemProp[BlogPost, Set[Assoc[User]]](
        "authors", _.authors, (p, authors) => p.copy(authors = authors)),
      new EmblemProp[BlogPost, Markdown]("content", _.content, (p, content) => p.copy(content = content)),
      new EmblemProp[BlogPost, Markdown]("slug", _.slug, (p, slug) => p.copy(slug = slug))
    )
  )

  override val assocLenses =
    lens1(emblem[Assoc[Blog]]("blog")) ::
    lensN(_.authors)({ (e, assoc) => e.copy(authors = assoc) }) ::
    Nil

}
