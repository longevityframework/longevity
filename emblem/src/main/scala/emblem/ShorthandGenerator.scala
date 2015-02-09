package emblem

import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._
import emblem.exceptions._

/** Generates a [[Shorthand shorthand]] from the corresponding [[TypeKey]] */
private class ShorthandGenerator[Actual : TypeKey, Abbreviated : TypeKey] extends Generator[Actual] {

  verifySingleParam()
  private val param: TermSymbol = params.head
  verifyExpectedAbbreviatedType()

  def generate: Shorthand[Actual, Abbreviated] = Shorthand(makeShorten(), makeUnshorten())

  private def verifySingleParam(): Unit = {
    if (params.size != 1) {
      throw new CaseClassHasMultipleParamsException(key)
    }
  }

  private def verifyExpectedAbbreviatedType(): Unit = {
    if (! (param.typeSignature =:= typeKey[Abbreviated].tpe)) {
      throw new UnexpectedAbbreviatedTypeException(key, typeKey[Abbreviated])
    }
  }

  private def makeShorten(): (Actual) => Abbreviated = makeGetFunction[Abbreviated](param.name)

  private def makeUnshorten(): (Abbreviated) => Actual = { abbreviated: Abbreviated =>
    module.applyMirror(abbreviated).asInstanceOf[Actual]
  }

}
