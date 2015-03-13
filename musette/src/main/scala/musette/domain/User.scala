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
extends RootEntity

object UserType extends RootEntityType[User]
