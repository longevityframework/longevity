package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[emblemFor emblem.emblemFor]] specifications */
class EmblemForSpec extends FlatSpec with GivenWhenThen with Matchers {

  import testData._

  behavior of "emblem.emblemFor"

  it should "throw exception on non case class types" in {
    intercept[EmblemGenerator.TypeIsNotCaseClassException] {
      emblemFor[NotACaseClass]
    }
  }

  it should "throw exception on case classes with multiple param lists" in {
    intercept[EmblemGenerator.CaseClassHasMultipleParamListsException] {
      emblemFor[MultipleParamLists]
    }
  }

  it should "throw exception on inner case classes" in {
    intercept[EmblemGenerator.CaseClassIsInnerClassException] {
      val hasInner = new HasInnerClass
      emblemFor[hasInner.IsInnerCaseClass]
    }
  }

}
