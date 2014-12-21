package musette.domain

import longevity.domain._

/** content authored by a site user. */
case class WikiPage(
  uri: Uri,
  authors: Set[Assoc[User]],
  wiki: Assoc[Wiki],
  content: Markdown,
  slug: Markdown
)
extends TopContent with Entity

object WikiPage extends EntityType[WikiPage] {

  override val assocLenses =
    lens1(_.wiki)({ (e, assoc) => e.copy(wiki = assoc) }) ::
    lenss(_.authors)({ (e, assoc) => e.copy(authors = assoc) }) ::
    Nil

}

