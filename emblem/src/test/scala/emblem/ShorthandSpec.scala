package emblem

import org.scalatest._
import emblem.exceptions._

/** [[Shorthand shorthand]] specifications */
class ShorthandSpec extends FlatSpec with GivenWhenThen with Matchers {

  import emblem.testData.genFailure._
  import emblem.testData.shorthands._

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
    intercept[UnexpectedAbbreviatedTypeException] {
      shorthandFor[Uri, Int]
    }
  }

  behavior of "the shorthand"

  it should "contain type keys for the actual and abbreviated types" in {
    uriShorthand.actualTypeKey should equal (typeKey[Uri])
    uriShorthand.abbreviatedTypeKey should equal (typeKey[String])
  }

  it should "provide conversion methods between the longhand and shorthand types" in {
    val uriString = "panda"
    val uri = Uri(uriString)
    uriShorthand.abbreviate(uri) should equal (uriString)
    uriShorthand.unabbreviate(uriString) should equal (uri)
  }

}
