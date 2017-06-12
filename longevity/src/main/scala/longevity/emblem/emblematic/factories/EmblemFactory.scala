package longevity.emblem.emblematic.factories

import longevity.emblem.emblematic.Emblem
import longevity.emblem.emblematic.EmblemProp
import typekey.TypeKey
import longevity.emblem.exceptions.CaseClassHasMultipleParamListsException
import longevity.emblem.exceptions.RequiredPropertyNotSetException
import longevity.emblem.reflectionUtil.makeTypeTag
import scala.reflect.runtime.universe.MethodSymbol
import scala.reflect.runtime.universe.NoSymbol
import scala.reflect.runtime.universe.TermName
import scala.reflect.runtime.universe.TermSymbol

/** generates an [[Emblem]] from the corresponding [[TypeKey]] */
private[emblem] class EmblemFactory[A : TypeKey] extends ReflectiveFactory[A] {

  /** generates the emblem */
  def generate: Emblem[A] = Emblem[A](
    key,
    params.map(_.name).map(emblemProp(_, params.size == 1)),
    makeCreator())

  private def emblemProp(name: TermName, isOnlyChild: Boolean): EmblemProp[A, _] = {
    val memberTerm: TermSymbol = tpe.member(name).asTerm.accessed.asTerm

    // the Any here is bogus. it comes back as something else
    val propTypeTag = makeTypeTag[Any](memberTerm, tag.mirror)

    val propKey = TypeKey(propTypeTag)
    makeEmblemProp(name, isOnlyChild)(propKey)
  }

  private def makeEmblemProp[B](
    name: TermName,
    isOnlyChild: Boolean)(
    implicit propKey: TypeKey[B]): EmblemProp[A, B] =
    EmblemProp[A, B](
      name.toString,
      getFunction[B](name),
      setFunction[B](name),
      isOnlyChild)(
      propKey)

  private def setFunction[B : TypeKey](name: TermName): (A, B) => A = {
    val setFunction = { (a: A, b: B) =>
      val args = params.map { param: TermSymbol =>
        if (param.name == name) {
          b
        }
        else {
          val getter = tpe.decl(param.name).asMethod
          val getterMirror = tag.mirror.reflect(a).reflectMethod(getter)
          getterMirror()
        }
      }
      module.applyMirror(args: _*).asInstanceOf[A]
    }
    setFunction
  }

  private def singleParamList(method: MethodSymbol) = {
    val methodParamLists = method.paramLists
    if (methodParamLists.size != 1) {
      throw new CaseClassHasMultipleParamListsException(key)
    }
    methodParamLists.head
  }

  private def makeCreator(): Map[String, Any] => A = {
    def caseObjectCreator = {
      val moduleMirror = tag.mirror.reflectModule(symbol.module.asModule)
      val instance = moduleMirror.instance.asInstanceOf[A]

      { map: Map[String, Any] => instance }
    }
    def caseClassCreator = { map: Map[String, Any] =>
      val args = params.zipWithIndex.map {
        case (param, index) =>
        val paramName: String = param.name.toString
        val value: Option[Any] = map.get(paramName)
        value match {
          case Some(a) => a
          case None => {
            val defaultMethod = module.typeSignature.member(TermName(s"apply$$default$$${index+1}"))
            if (defaultMethod == NoSymbol) {
              throw new RequiredPropertyNotSetException(paramName)
            }
            val defaultMirror = module.instanceMirror.reflectMethod(defaultMethod.asMethod)
            defaultMirror()
          }
        }
      }
      module.applyMirror(args: _*).asInstanceOf[A]
    }

    if (symbol.isModuleClass) caseObjectCreator else caseClassCreator
  }

}
