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

  private lazy val uriProp =
    new EmblemProp[BlogPost, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri))

  private lazy val blogProp =
    new EmblemProp[BlogPost, Assoc[Blog]]("blog", _.blog, (p, blog) => p.copy(blog = blog))

  private lazy val authorsProp =
    new EmblemProp[BlogPost, Set[Assoc[User]]]("authors", _.authors, (p, authors) => p.copy(authors = authors))

  private lazy val contentProp =
    new EmblemProp[BlogPost, Markdown]("content", _.content, (p, content) => p.copy(content = content))

  private lazy val slugProp =
    new EmblemProp[BlogPost, Markdown]("slug", _.slug, (p, slug) => p.copy(slug = slug))

  lazy val emblem = new Emblem[BlogPost](
    "musette.domain",
    "BlogPost",
    Seq(uriProp, blogProp, authorsProp, contentProp, slugProp),
    EmblemPropToValueMap(),
    { map =>
      BlogPost(
        map.get(uriProp),
        map.get(blogProp),
        map.get(authorsProp),
        map.get(contentProp),
        map.get(slugProp))
    }
  )

}
