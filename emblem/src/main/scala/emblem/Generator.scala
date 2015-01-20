package emblem

import scala.reflect.ClassTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._
import emblem.stringUtil._

/** a useful scope to hang on to various data to be shared across methods, so we don't have to recompute them
 * or pass them around in massive parameter lists */
private abstract class Generator[A : TypeKey] {

  protected val key = implicitly[TypeKey[A]]
  protected val tpe = key.tpe
  protected implicit val tag = key.tag
  protected implicit val classTag = typeTagToClassTag[A]
  protected val symbol = tpe.typeSymbol.asClass
  verifyIsCaseClass()
  verifyIsNotInnerClass()
  protected val constructorSymbol = symbol.primaryConstructor.asMethod
  verifySingleParamList()
  protected val params: List[TermSymbol] = constructorSymbol.paramLists.head.map(_.asTerm)

  protected object module {
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

  // following FixedMirrorTypeCreator in
  // https://github.com/scala/scala/blob/2.11.x/src/reflect/scala/reflect/internal/StdCreators.scala
  protected def makeTypeTag[A](term: TermSymbol): TypeTag[A] = {
    val tpe = term.typeSignature
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

  protected def makeGetFunction[U : TypeKey](name: TermName): (A) => U = {
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

  private def verifyIsCaseClass(): Unit = {
    if (!symbol.isClass || !symbol.asClass.isCaseClass) {
      throw new TypeIsNotCaseClassException(key)
    }
  }

  private def verifyIsNotInnerClass(): Unit = {
    if (!symbol.isStatic) {
      throw new CaseClassIsInnerClassException(key)
    }
  }

  private def verifySingleParamList(): Unit = {
    if (constructorSymbol.paramLists.size != 1) {
      throw new CaseClassHasMultipleParamListsException(key)
    }
  }

}
