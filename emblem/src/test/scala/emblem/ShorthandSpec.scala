package emblem

import scala.reflect.runtime.universe._
import org.scalatest._
import org.scalatest.OptionValues._

/** [[Shorthand shorthand]] specifications */
class ShorthandSpec extends FlatSpec with GivenWhenThen with Matchers {

  import emblem.testData._

  behavior of "emblem.shorthandFor"

  it should "throw exception on non case class types" in {
    intercept[TypeIsNotCaseClassException] {
      shorthandFor[NotACaseClass, Any]
    }
  }

  it should "throw exception on case classes with multiple param lists" in {
    intercept[CaseClassHasMultipleParamListsException] {
      shorthandFor[MultipleParamLists, Any]
    }
  }

  it should "throw exception on inner case classes" in {
    intercept[CaseClassIsInnerClassException] {
      val hasInner = new HasInnerClass
      shorthandFor[hasInner.IsInnerCaseClass, Any]
    }
  }

  it should "throw exception on case classes with multiple params (one param list)" in {
    intercept[CaseClassHasMultipleParamsException] {
      shorthandFor[MultipleParams, Any]
    }
  }

  it should "throw exception when the requested short type does not match the case class parameter" in {
    intercept[UnexpectedShortTypeException] {
      shorthandFor[Uri, Int]
    }
  }

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
