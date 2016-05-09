package emblem.emblematic

import emblem.exceptions.CaseClassHasMultipleParamListsException
import emblem.exceptions.CaseClassIsInnerClassException
import emblem.exceptions.TypeIsNotCaseClassException
import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for cases where [[Emblem.apply Emblem generation]] should fail.
 *
 * non-error cases for emblem generation are covered elsewhere in the test suite.
 */
class EmblemGenerationFailuresSpec extends FlatSpec with GivenWhenThen with Matchers {

  import emblem.testData.genFailure._

  behavior of "emblem.emblematic.Emblem"

  it should "throw exception on non case class types" in {
    intercept[TypeIsNotCaseClassException] {
      Emblem[NotACaseClass]
    }
  }

  it should "throw exception on case classes with multiple param lists" in {
    intercept[CaseClassHasMultipleParamListsException] {
      Emblem[MultipleParamLists]
    }
  }

  it should "throw exception on inner case classes" in {
    intercept[CaseClassIsInnerClassException] {
      val hasInner = new HasInnerClass
      Emblem[hasInner.IsInnerCaseClass]
    }
  }

}
