package emblem.testData

import emblem.HasEmblem

/** for emblem and extractor failure cases */
object genFailure {

  trait NotACaseClass extends HasEmblem

  case class MultipleParamLists(i: Int)(j: Int) extends HasEmblem

  class HasInnerClass {
    case class IsInnerCaseClass(i: Int) extends HasEmblem
  }

  case class MultipleParams(i: Int, j: Int)

}
