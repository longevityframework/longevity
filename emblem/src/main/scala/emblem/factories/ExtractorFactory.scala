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
extends ReflectiveFactory[Range] {

  verifySingleParam()
  private val param: TermSymbol = params.head
  verifyExpectedDomainType()

  def generate: Extractor[Domain, Range] = Extractor(makeApply(), makeUnapply())

  private def verifySingleParam(): Unit = {
    if (params.size != 1) {
      throw new CaseClassHasMultipleParamsException(key)
    }
  }

  private def verifyExpectedDomainType(): Unit = {
    if (! (param.typeSignature =:= typeKey[Domain].tpe)) {
      throw new UnexpectedDomainTypeException(key, typeKey[Domain])
    }
  }

  private def makeUnapply(): (Range) => Domain = makeGetFunction[Domain](param.name)

  private def makeApply(): (Domain) => Range = { domain: Domain =>
    module.applyMirror(domain).asInstanceOf[Range]
  }

}
