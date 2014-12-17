package musette.domain

import longevity.domain._

object User extends EntityType[User]

case class User(
  uri: Uri,
  email: String,
  handle: String,
  slug: Markdown
)
extends Entity

