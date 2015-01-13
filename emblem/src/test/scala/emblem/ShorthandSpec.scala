package emblem

import scala.reflect.runtime.universe._
import org.scalatest._
import org.scalatest.OptionValues._

/** shorthand specifications */
class ShorthandSpec extends FlatSpec with GivenWhenThen with Matchers {

  case class Uri(uri: String)

  private val uriShorthand = Shorthand[Uri, String](_.uri, Uri(_))

  behavior of "the shorthand"

  it should "contain type keys for the longhand and shorthand types" in {
    uriShorthand.longTypeKey should equal (typeKey[Uri])
    uriShorthand.shortTypeKey should equal (typeKey[String])
  }

  it should "provide conversion methods between the longhand and shorthand types" in {
    val uriString = "panda"
    val uri = Uri(uriString)
    uriShorthand.shorten(uri) should equal (uriString)
    uriShorthand.unshorten(uriString) should equal (uri)
  }

}
