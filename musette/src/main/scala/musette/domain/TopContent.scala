package musette.domain

import longevity.domain._

/** content that is typically displayed on its own webpage. */
trait TopContent extends Content {
  val slug: Markdown
}
