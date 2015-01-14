package musette.domain

import emblem._
import longevity.domain._

/** a blog. */
case class Blog(
  uri: Uri,
  site: Assoc[Site],
  authors: Set[Assoc[User]],
  slug: Markdown
)
extends SiteSection with Entity

object Blog extends EntityType[Blog] {

  private lazy val uriProp =
    new EmblemProp[Blog, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri))
  private lazy val siteProp =
      new EmblemProp[Blog, Assoc[Site]]("site", _.site, (p, site) => p.copy(site = site))
  private lazy val authorsProp =
        new EmblemProp[Blog, Set[Assoc[User]]]("authors", _.authors, (p, authors) => p.copy(authors = authors))
  private lazy val slugProp =
          new EmblemProp[Blog, Markdown]("slug", _.slug, (p, slug) => p.copy(slug = slug))

  lazy val emblem = new Emblem[Blog](
    "musette.domain",
    "Blog",
    Seq(uriProp, siteProp, authorsProp, slugProp),
    EmblemPropToValueMap(),
    { map =>
      Blog(
        map.get(uriProp),
        map.get(siteProp),
        map.get(authorsProp),
        map.get(slugProp))
    }
  )

}
