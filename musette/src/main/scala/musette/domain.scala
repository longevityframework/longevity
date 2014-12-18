package musette

package object domain {

  // TODO: basic validity checking for these implicits:

  implicit class Email(email: String)

  implicit class Markdown(markdown: String)

  implicit class Uri(uri: String)

}
