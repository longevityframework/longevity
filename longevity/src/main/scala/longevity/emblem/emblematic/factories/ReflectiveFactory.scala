package longevity.emblem.emblematic.factories

import typekey.TypeKey
import longevity.emblem.exceptions.CaseClassHasMultipleParamListsException
import longevity.emblem.reflectionUtil.TypeReflector
import scala.reflect.runtime.universe.TermSymbol

/** a useful scope to hang on to various data to be shared across methods, so we
 * don't have to recompute them or pass them around in massive parameter lists
 */
private[emblem] abstract class ReflectiveFactory[A : TypeKey] extends TypeReflector[A] {

  verifyIsCaseClass()
  verifyIsNotInnerClass()

  protected val constructorSymbol = symbol.primaryConstructor.asMethod
  verifySingleParamList()

  protected val params: List[TermSymbol] = constructorSymbol.paramLists.head.map(_.asTerm)

  private def verifySingleParamList(): Unit = {
    if (constructorSymbol.paramLists.size != 1) {
      throw new CaseClassHasMultipleParamListsException(key)
    }
  }

}
