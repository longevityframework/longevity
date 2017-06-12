package longevity.emblem.testData



/** for emblem and extractor failure cases */
object genFailure {

  trait NotACaseClass

  case class MultipleParamLists(i: Int)(j: Int)

  class HasInnerClass {
    case class IsInnerCaseClass(i: Int)
  }

  case class MultipleParams(i: Int, j: Int)

}
