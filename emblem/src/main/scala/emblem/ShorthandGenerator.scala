package emblem

import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._

/** a useful scope to hang on to various data to be shared across methods, so we don't have to recompute them
 * or pass them around in massive parameter lists */
@throws[GeneratorException]
private class ShorthandGenerator[Long : TypeKey, Short : TypeKey] extends Generator[Long] {

  verifySingleParam()
  private val param: TermSymbol = params.head
  verifyExpectedShortType()

  def generate: Shorthand[Long, Short] = Shorthand(makeShorten(), makeUnshorten())

  private def verifySingleParam(): Unit = {
    if (params.size != 1) {
      throw new CaseClassHasMultipleParamsException(key)
    }
  }

  private def verifyExpectedShortType(): Unit = {
    if (! (param.typeSignature =:= typeKey[Short].tpe)) {
      throw new UnexpectedShortTypeException(key, typeKey[Short])
    }
  }

  private def makeShorten(): (Long) => Short = makeGetFunction[Short](param.name)

  private def makeUnshorten(): (Short) => Long = { short: Short =>
    module.applyMirror(short).asInstanceOf[Long]
  }

}
