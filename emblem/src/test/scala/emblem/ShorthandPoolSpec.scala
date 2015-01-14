package emblem

import scala.reflect.runtime.universe._
import org.scalatest._
import org.scalatest.OptionValues._

/** [[ShorthandPool shorthand pool]] specifications */
class ShorthandPoolSpec extends FlatSpec with GivenWhenThen with Matchers {

  case class Uri(uri: String)
  case class Email(email: String)
  case class Markdown(markdown: String)

  private val uriShorthand = Shorthand[Uri, String](_.uri, Uri(_))
  private val emailShorthand = Shorthand[Email, String](_.email, Email(_))

  private val pool = ShorthandPool(uriShorthand, emailShorthand)

  behavior of "the shorthand pool"

  it should "provide a sequence of shorthands in the pool" in {
    val shorthandSeq = pool.shorthands
    shorthandSeq.size should equal (2)
    shorthandSeq should contain (uriShorthand)
    shorthandSeq should contain (emailShorthand)
  }

  it should "provide a means to look up shorthands by Long type" in {
    pool.longTypeKeyToShorthand(typeKey[Uri]).value should equal (uriShorthand)
    pool.longTypeKeyToShorthand(typeKey[Email]).value should equal (emailShorthand)
    pool.longTypeKeyToShorthand(typeKey[Markdown]) should equal (None)
  }

  behavior of "the shorthand pool constructor"
  it should "throw exception if there are multiple longhands represented in the pool" in {
    val extraneousShorthand = Shorthand[Email, String](_.email, Email(_))
    intercept[ShorthandPool.DuplicateShorthandsException] {
      ShorthandPool(uriShorthand, emailShorthand, extraneousShorthand)
    }
  }

}
