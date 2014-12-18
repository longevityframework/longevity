package musette.domain

import longevity.domain._

case class User(
  uri: Uri,
  site: Assoc[Site],
  email: Email,
  handle: String,
  slug: Markdown
)
extends Entity

object User extends EntityType[User]

