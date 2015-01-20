package emblem

import scala.reflect.ClassTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._
import emblem.stringUtil._

/** a useful scope to hang on to various data to be shared across methods, so we don't have to recompute them
 * or pass them around in massive parameter lists */
@throws[GeneratorException]
private class EmblemGenerator[A <: HasEmblem : TypeKey] extends Generator[A] {

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

  private def makeEmblemProp[U : TypeKey](name: TermName)(propKey: TypeKey[U]): EmblemProp[A, U] =
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

  private def makeCreator(): EmblemPropToValueMap[A] => A = {
    val creator = { map: EmblemPropToValueMap[A] =>
      val args = params.zipWithIndex.map {
        case (param: TermSymbol, index: Int) =>
        val paramName: String = param.name.toString          
        val value: Option[Any] = map.getOptionByName(paramName)
        value match {
          case Some(a) => a
          case None => {
            val defaultMethod = module.typeSignature.member(TermName(s"apply$$default$$${index+1}"))
            if (defaultMethod == NoSymbol) {
              throw new EmblemPropToValueMap.NoValueForPropName(paramName, map)
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
