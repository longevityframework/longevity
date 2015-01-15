package musette.repo

import reactivemongo.bson._
import musette.domain._

package object mongo {

  // TODO: these handlers are no longer needed or used. remove

  // TODO: dry. at worst, give a common super and replace the objects below with a single def

  implicit object EmailHandler extends BSONHandler[BSONString, Email] {
    def read(email: BSONString) = Email(email.value)
    def write(email: Email) = BSONString(email.toString)
  }

  implicit object MarkdownHandler extends BSONHandler[BSONString, Markdown] {
    def read(markdown: BSONString) = Markdown(markdown.value)
    def write(markdown: Markdown) = BSONString(markdown.toString)
  }

  implicit object UriHandler extends BSONHandler[BSONString, Uri] {
    def read(uri: BSONString) = Uri(uri.value)
    def write(uri: Uri) = BSONString(uri.toString)
  }

}

