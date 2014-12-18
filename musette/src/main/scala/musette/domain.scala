package musette

package object domain {

  // TODO: basic validity checking for these implicits:

  implicit class Email(email: String) {
    override def toString = email
  }

  implicit class Markdown(markdown: String) {
    override def toString = markdown
  }

  implicit class Uri(uri: String) {
    override def toString = uri
  }

}
