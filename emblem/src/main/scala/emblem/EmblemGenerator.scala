package emblem

import scala.reflect.ClassTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._
import emblem.stringUtil._

object EmblemGenerator {

  /** an exception indicating you broke the contract of [[emblem.emblemFor]] */
  class EmblemGeneratorException(message: String) extends Exception(message)

  class TypeIsNotCaseClassException(val key: TypeKey[_ <: HasEmblem])
  extends EmblemGeneratorException(s"emblems for non-case classes is currently not supported: $key")

  class CaseClassHasMultipleParamListsException(val key: TypeKey[_ <: HasEmblem])
  extends EmblemGeneratorException(
    s"emblems for case classes with extra param lists currently not supported: $key")

  class CaseClassIsInnerClassException(val key: TypeKey[_ <: HasEmblem])
  extends EmblemGeneratorException(s"emblems for inner case classes currently not supported: $key")

}

/** a useful scope to hang on to various data to be shared across methods, so we don't have to recompute them
 * or pass them around in massive parameter lists */
private class EmblemGenerator[A <: HasEmblem : TypeKey] {

  private val key = implicitly[TypeKey[A]]
  private val tpe = key.tpe
  private implicit val tag = key.tag
  private implicit val classTag = typeTagToClassTag[A]
  private val symbol = tpe.typeSymbol.asClass
  verifyIsCaseClass()
  verifyIsNotInnerClass()
  private val constructorSymbol = symbol.primaryConstructor.asMethod
  verifySingleParamList()
  private val params: List[TermSymbol] = constructorSymbol.paramLists.head.map(_.asTerm)

  private object module {
    val instanceMirror = {
      val classSymbol: ClassSymbol = tpe.typeSymbol.asClass
      val moduleSymbol: ModuleSymbol = classSymbol.companion.asModule
      val moduleMirror: ModuleMirror = currentMirror.reflectModule(moduleSymbol)
      val instance: Any = moduleMirror.instance
      currentMirror.reflect(instance)
    }
    val typeSignature = instanceMirror.symbol.typeSignature
    val applyMirror = {
      val applyMethod = typeSignature.member(TermName("apply")).asMethod
      instanceMirror.reflectMethod(applyMethod)
    }
  }

  @throws[EmblemGenerator.EmblemGeneratorException]
  def generate: Emblem[A] = {
    Emblem[A](
      typeNamePrefix(tpe),
      typeName(tpe),
      params.map(_.name).map(emblemProp(_)),
      makeCreator())
  }

  private def verifyIsCaseClass(): Unit = {
    if (!symbol.isClass || !symbol.asClass.isCaseClass) {
      throw new EmblemGenerator.TypeIsNotCaseClassException(key)
    }
  }

  private def verifyIsNotInnerClass(): Unit = {
    if (!symbol.isStatic) {
      throw new EmblemGenerator.CaseClassIsInnerClassException(key)
    }
  }

  private def verifySingleParamList(): Unit = {
    if (constructorSymbol.paramLists.size != 1) {
      throw new EmblemGenerator.CaseClassHasMultipleParamListsException(key)
    }
  }

  private def emblemProp(name: TermName): EmblemProp[A, _] = {
    val memberTerm = tpe.member(name).asTerm.accessed.asTerm
    val propType = memberTerm.typeSignature
    val propTypeTag = makeTypeTag[Any](propType) // the Any here is bogus
    val propKey = TypeKey(propTypeTag)
    makeEmblemProp(name)(propKey)
  }

  // following FixedMirrorTypeCreator in
  // https://github.com/scala/scala/blob/2.11.x/src/reflect/scala/reflect/internal/StdCreators.scala
  private def makeTypeTag[A](tpe: Type): TypeTag[A] = {
    import scala.reflect.api.Mirror
    import scala.reflect.api.TypeCreator
    import scala.reflect.api.Universe
    val typeCreator = new TypeCreator {
      def apply[U <: Universe with Singleton](m: Mirror[U]): U # Type =
        if (m eq currentMirror)
          tpe.asInstanceOf[U # Type]
        else
          throw new IllegalArgumentException(
            s"Type tag defined in $currentMirror cannot be migrated to other mirrors.")
    }
    TypeTag[A](currentMirror, typeCreator)
  }

  private def makeEmblemProp[U : TypeKey](name: TermName)(propKey: TypeKey[U]): EmblemProp[A, U] = {
    EmblemProp[A, U](
      name.toString,
      makeGetFunction[U](name),
      makeSetFunction[U](name))(
      key,
      propKey)
  }

  private def makeGetFunction[U : TypeKey](name: TermName): (A) => U = {
    val getter = tpe.decl(name).asMethod
    val getFunction = { a: A =>
      val instanceMirror = currentMirror.reflect(a)
      val methodMirror = instanceMirror.reflectMethod(getter)
      methodMirror().asInstanceOf[U]
    }
    getFunction
  }

  private def typeTagToClassTag[T : TypeTag]: ClassTag[T] = {
    ClassTag[T](typeTag[T].mirror.runtimeClass(typeTag[T].tpe))
  }

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
      throw new EmblemGenerator.CaseClassHasMultipleParamListsException(key)
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
