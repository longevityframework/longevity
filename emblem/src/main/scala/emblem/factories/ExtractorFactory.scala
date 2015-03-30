package emblem.factories

import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._
import emblem._
import emblem.exceptions._

/** Generates a [[Extractor extractor]] from the corresponding [[TypeKey]] */
private[emblem] class ExtractorFactory[Actual : TypeKey, Abbreviated : TypeKey]
extends ReflectiveFactory[Actual] {

  verifySingleParam()
  private val param: TermSymbol = params.head
  verifyExpectedAbbreviatedType()

  def generate: Extractor[Actual, Abbreviated] = Extractor(makeShorten(), makeUnshorten())

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
