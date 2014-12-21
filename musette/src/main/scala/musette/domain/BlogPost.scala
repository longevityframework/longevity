package musette.domain

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

  override val assocLenses =
    lens1(_.blog)({ (e, assoc) => e.copy(blog = assoc) }) ::
    lensN(_.authors)({ (e, assoc) => e.copy(authors = assoc) }) ::
    Nil

}
