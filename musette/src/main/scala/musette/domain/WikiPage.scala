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

  private lazy val uriProp =
    new EmblemProp[WikiPage, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri))

  private lazy val wikiProp =
    new EmblemProp[WikiPage, Assoc[Wiki]]("wiki", _.wiki, (p, wiki) => p.copy(wiki = wiki))

  private lazy val authorsProp =
    new EmblemProp[WikiPage, Set[Assoc[User]]]("authors", _.authors, (p, authors) => p.copy(authors = authors))

  private lazy val contentProp =
    new EmblemProp[WikiPage, Markdown]("content", _.content, (p, content) => p.copy(content = content))

  private lazy val slugProp =
    new EmblemProp[WikiPage, Markdown]("slug", _.slug, (p, slug) => p.copy(slug = slug))

  lazy val emblem = new Emblem[WikiPage](
    "musette.domain",
    "WikiPage",
    Seq(uriProp, wikiProp, authorsProp, contentProp, slugProp),
    EmblemPropToValueMap(),
    { map =>
      WikiPage(
        map.get(uriProp),
        map.get(wikiProp),
        map.get(authorsProp),
        map.get(contentProp),
        map.get(slugProp))
    }
  )

}

