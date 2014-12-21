package musette.domain

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

  override val assocLenses =
    lens1(_.site)({ (e, assoc) => e.copy(site = assoc) }) ::
    lenss(_.authors)({ (e, assoc) => e.copy(authors = assoc) }) ::
    Nil

}

