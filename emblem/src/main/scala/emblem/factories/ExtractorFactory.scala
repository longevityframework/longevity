package emblem.factories

import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._
import emblem._
import emblem.exceptions._

/** generates an [[Extractor extractor]] from [[TypeKey type keys]] for the `Domain` and `Range` types
 * @tparam Range the range type
 * @tparam Domain the domain type
 */
private[emblem] class ExtractorFactory[Domain : TypeKey, Range : TypeKey]
extends ReflectiveFactory[Domain] {

  verifySingleParam()
  private val param: TermSymbol = params.head
  verifyExpectedDomainType()

  def generate: Extractor[Domain, Range] = Extractor(makeApply(), makeInverse())

  private def verifySingleParam(): Unit = {
    if (params.size != 1) {
      throw new CaseClassHasMultipleParamsException(key)
    }
  }

  private def verifyExpectedDomainType(): Unit = {
    if (! (param.typeSignature =:= typeKey[Range].tpe)) {
      throw new UnexpectedDomainTypeException(key, typeKey[Domain])
    }
  }

  private def makeApply(): (Domain) => Range = makeGetFunction[Range](param.name)

  private def makeInverse(): (Range) => Domain = { range: Range =>
    module.applyMirror(range).asInstanceOf[Domain]
  }

}
