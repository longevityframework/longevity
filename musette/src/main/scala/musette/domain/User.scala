package musette.domain

import emblem._
import longevity.domain._

case class User(
  uri: Uri,
  site: Assoc[Site],
  email: Email,
  handle: String,
  slug: Markdown
)
extends Entity

object User extends EntityType[User] {

  lazy val emblem = new Emblem[User](
    "musette.domain",
    "User",
    Seq(
      new EmblemProp[User, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri)),
      new EmblemProp[User, Assoc[Site]]("site", _.site, (p, site) => p.copy(site = site)),
      new EmblemProp[User, Email]("email", _.email, (p, email) => p.copy(email = email)),
      new EmblemProp[User, String]("handle", _.handle, (p, handle) => p.copy(handle = handle)),
      new EmblemProp[User, Markdown]("slug", _.slug, (p, slug) => p.copy(slug = slug))
    )
  )

  override val assocLenses =
    lens1(emblem[Assoc[Site]]("site")) ::
    Nil

}

