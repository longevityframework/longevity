package musette

package object domain {

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
