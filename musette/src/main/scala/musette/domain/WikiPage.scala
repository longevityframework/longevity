package musette.domain

import emblem._
import longevity.domain._

/** content authored by a site user. */
case class WikiPage(
  uri: Uri,
  wiki: Assoc[Wiki],
  authors: Set[Assoc[User]],
  content: Markdown,
  slug: Markdown
)
extends TopContent with Entity

object WikiPage extends EntityType[WikiPage] {

  lazy val emblem = new Emblem[WikiPage](
    "musette.domain",
    "WikiPage",
    Seq(
      new EmblemProp[WikiPage, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri)),
      new EmblemProp[WikiPage, Assoc[Wiki]]("wiki", _.wiki, (p, wiki) => p.copy(wiki = wiki)),
      new EmblemProp[WikiPage, Set[Assoc[User]]](
        "authors", _.authors, (p, authors) => p.copy(authors = authors)),
      new EmblemProp[WikiPage, Markdown]("content", _.content, (p, content) => p.copy(content = content)),
      new EmblemProp[WikiPage, Markdown]("slug", _.slug, (p, slug) => p.copy(slug = slug))
    )
  )

  override val assocLenses =
    lens1(emblem[Assoc[Wiki]]("wiki")) ::
    lensN(_.authors)({ (e, assoc) => e.copy(authors = assoc) }) ::
    Nil

}

