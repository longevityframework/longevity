package emblem.factories

import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._
import emblem._
import emblem.exceptions._

/** Generates a [[Extractor extractor]] from the corresponding [[TypeKey]] */
private[emblem] class ExtractorFactory[Domain : TypeKey, Range : TypeKey]
extends ReflectiveFactory[Range] {

  verifySingleParam()
  private val param: TermSymbol = params.head
  verifyExpectedDomainType()

  // TODO rename makeShorten makeUnshorten

  def generate: Extractor[Domain, Range] = Extractor(makeUnshorten(), makeShorten())

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

  private def makeShorten(): (Range) => Domain = makeGetFunction[Domain](param.name)

  private def makeUnshorten(): (Domain) => Range = { domain: Domain =>
    module.applyMirror(domain).asInstanceOf[Range]
  }

}
