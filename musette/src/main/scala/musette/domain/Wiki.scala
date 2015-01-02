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

  lazy val emblem = new Emblem[Wiki](
    "musette.domain",
    "Wiki",
    Seq(
      new EmblemProp[Wiki, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri)),
      new EmblemProp[Wiki, Assoc[Site]]("site", _.site, (p, site) => p.copy(site = site)),
      new EmblemProp[Wiki, Set[Assoc[User]]]("authors", _.authors, (p, authors) => p.copy(authors = authors)),
      new EmblemProp[Wiki, Markdown]("slug", _.slug, (p, slug) => p.copy(slug = slug))
    )
  )

  override val assocLenses =
    lens1(emblem[Assoc[Site]]("site")) ::
    lensN(_.authors)({ (e, assoc) => e.copy(authors = assoc) }) ::
    Nil

}

