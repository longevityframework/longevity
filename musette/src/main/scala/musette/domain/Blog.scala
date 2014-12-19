package musette.domain

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

  override val assocLenses =
    lens(_.site)({ (e, assoc) => e.copy(site = assoc) }) ::
    lenss(_.authors)({ (e, assoc) => e.copy(authors = assoc) }) ::
    Nil

}
