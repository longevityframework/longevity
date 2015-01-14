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

  private lazy val uriProp =
    new EmblemProp[User, Uri]("uri", _.uri, (p, uri) => p.copy(uri = uri))

  private lazy val siteProp =
    new EmblemProp[User, Assoc[Site]]("site", _.site, (p, site) => p.copy(site = site))

  private lazy val emailProp =
    new EmblemProp[User, Email]("email", _.email, (p, email) => p.copy(email = email))

  private lazy val handleProp =
    new EmblemProp[User, String]("handle", _.handle, (p, handle) => p.copy(handle = handle))

  private lazy val slugProp =
    new EmblemProp[User, Markdown]("slug", _.slug, (p, slug) => p.copy(slug = slug))

  lazy val emblem = new Emblem[User](
    "musette.domain",
    "User",
    Seq(uriProp, siteProp, emailProp, handleProp, slugProp),
    EmblemPropToValueMap(),
    { map =>
      User(
        map.get(uriProp),
        map.get(siteProp),
        map.get(emailProp),
        map.get(handleProp),
        map.get(slugProp))
    }
  )

}

