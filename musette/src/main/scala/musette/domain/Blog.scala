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

  val emblem = new Emblem[Blog](
    "musette.domain",
    "Blog",
    Seq(
      new EmblemProp[Blog, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri)),
      new EmblemProp[Blog, Assoc[Site]]("site", _.site, (p, site) => p.copy(site = site)),
      new EmblemProp[Blog, Set[Assoc[User]]]("authors", _.authors, (p, authors) => p.copy(authors = authors)),
      new EmblemProp[Blog, Markdown]("slug", _.slug, (p, slug) => p.copy(slug = slug))
    )
  )

  override val assocLenses =
    lens1(_.site)({ (e, assoc) => e.copy(site = assoc) }) ::
    lensN(_.authors)({ (e, assoc) => e.copy(authors = assoc) }) ::
    Nil

}
