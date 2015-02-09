package emblem

import org.scalatest._
import org.scalatest.OptionValues._
import emblem.exceptions.DuplicateShorthandsException

/** [[ShorthandPool shorthand pool]] specifications */
class ShorthandPoolSpec extends FlatSpec with GivenWhenThen with Matchers {

  import emblem.testData.shorthands._

  behavior of "the shorthand pool"

  it should "provide a sequence of shorthands in the pool" in {
    val shorthandSeq = shorthandPool.values
    shorthandSeq.size should equal (5)
    shorthandSeq should contain (uriShorthand)
    shorthandSeq should contain (emailShorthand)
  }

  it should "provide a means to look up shorthands by Long type" in {
    shorthandPool.get(typeKey[Uri]).value should equal (uriShorthand)
    shorthandPool.get(typeKey[Email]).value should equal (emailShorthand)
    shorthandPool.get(typeKey[NoShorthand]) should equal (None)
  }

  behavior of "the shorthand pool constructor"
  it should "throw exception if there are multiple longhands represented in the pool" in {
    val extraneousShorthand = shorthandFor[Email, String]
    intercept[DuplicateShorthandsException] {
      ShorthandPool(uriShorthand, emailShorthand, extraneousShorthand)
    }
  }

}
