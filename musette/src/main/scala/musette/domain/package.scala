package musette

import emblem._

package object domain {

  val shorthands = emblem.ShorthandPool(
    Shorthand[Email, String](_.toString, Email(_)),
    Shorthand[Markdown, String](_.toString, Markdown(_)),
    Shorthand[Uri, String](_.toString, Uri(_))
  )

  // TODO: basic validity checking for these implicits:

  implicit class Email(private val email: String) extends AnyVal {
    override def toString = email
  }

  implicit class Markdown(private val markdown: String) extends AnyVal {
    override def toString = markdown
  }

  implicit class Uri(private val uri: String) extends AnyVal {
    override def toString = uri
  }

}
