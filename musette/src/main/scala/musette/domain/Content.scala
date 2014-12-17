package musette.domain

import longevity.domain._

/** content authored by a site user. */
trait Content {
  val uri: Uri
  val author: Assoc[User]
  val content: Markdown
}
