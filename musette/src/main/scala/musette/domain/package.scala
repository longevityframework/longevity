package musette

import scala.language.implicitConversions
import emblem._

package object domain {

  val shorthands = emblem.ShorthandPool(
    Shorthand[Email, String](_.email, Email(_)),
    Shorthand[Markdown, String](_.markdown, Markdown(_)),
    Shorthand[Uri, String](_.uri, Uri(_))
  )

  implicit def stringToEmail(email: String): Email = Email(email)

  implicit def stringToMarkdown(markdown: String): Markdown = Markdown(markdown)

  implicit def stringToUri(uri: String): Uri = Uri(uri)

}
