package musette.domain

import emblem._
import longevity.domain._

/** a wiki. */
case class Wiki(
  uri: Uri,
  site: Assoc[Site],
  authors: Set[Assoc[User]],
  slug: Markdown
)
extends SiteSection with Entity

object Wiki extends EntityType[Wiki] {

  private lazy val uriProp =
    new EmblemProp[Wiki, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri))

  private lazy val siteProp =
    new EmblemProp[Wiki, Assoc[Site]]("site", _.site, (p, site) => p.copy(site = site))

  private lazy val authorsProp =
    new EmblemProp[Wiki, Set[Assoc[User]]]("authors", _.authors, (p, authors) => p.copy(authors = authors))

  private lazy val slugProp =
    new EmblemProp[Wiki, Markdown]("slug", _.slug, (p, slug) => p.copy(slug = slug))

  lazy val emblem = new Emblem[Wiki](
    "musette.domain",
    "Wiki",
    Seq(uriProp, siteProp, authorsProp, slugProp),
    EmblemPropToValueMap(),
    { map =>
      Wiki(
        map.get(uriProp),
        map.get(siteProp),
        map.get(authorsProp),
        map.get(slugProp))
    }
  )

}

