package emblem.factories

import scala.reflect.ClassTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._
import emblem.imports._
import emblem.exceptions._
import emblem.stringUtil._
import emblem.reflectionUtil.makeTypeTag

/** generates an [[Emblem]] from the corresponding [[TypeKey]] */
private[emblem] class EmblemFactory[A <: HasEmblem : TypeKey] extends ReflectiveFactory[A] {

  /** generates the emblem */
  def generate: Emblem[A] = Emblem[A](
    typeNamePrefix(tpe),
    typeName(tpe),
    params.map(_.name).map(emblemProp(_)),
    makeCreator())

  private def emblemProp(name: TermName): EmblemProp[A, _] = {
    val memberTerm: TermSymbol = tpe.member(name).asTerm.accessed.asTerm
    val propTypeTag = makeTypeTag[Any](memberTerm) // the Any here is bogus. it comes back as something else
    val propKey = TypeKey(propTypeTag)
    makeEmblemProp(name)(propKey)
  }

  private def makeEmblemProp[U](name: TermName)(implicit propKey: TypeKey[U]): EmblemProp[A, U] =
    EmblemProp[A, U](
      name.toString,
      makeGetFunction[U](name),
      makeSetFunction[U](name))(
      key,
      propKey)

  private def makeSetFunction[U : TypeKey](name: TermName): (A, U) => A = {
    val setFunction = { (a: A, u: U) =>
      val args = params.map { param: TermSymbol =>
        if (param.name == name) {
          u
        }
        else {
          val getter = tpe.decl(param.name).asMethod
          val getterMirror = currentMirror.reflect(a).reflectMethod(getter)
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
    val creator = { map: Map[String, Any] =>
      val args = params.zipWithIndex.map {
        case (param: TermSymbol, index: Int) =>
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
    creator
  }

}
