package emblem
 
import scala.ScalaReflectionException
import scala.reflect.ClassTag
import scala.reflect.api.Mirror
import scala.reflect.api.TypeCreator
import scala.reflect.api.Universe
import scala.reflect.runtime.universe.InstanceMirror
import scala.reflect.runtime.universe.ModuleMirror
import scala.reflect.runtime.universe.ModuleSymbol
import scala.reflect.runtime.universe.Symbol
import scala.reflect.runtime.universe.TermName
import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeTag
import scala.reflect.runtime.universe.RuntimeMirror

/** generally useful utility functions for working with Scala reflection library */
package object reflectionUtil {

  // overloaded makeTypeTag follows FixedMirrorTypeCreator in
  // https://github.com/scala/scala/blob/2.11.x/src/reflect/scala/reflect/internal/StdCreators.scala

  /** makes a type tag for a term */
  def makeTypeTag[A](term: Symbol, runtimeMirror: RuntimeMirror): TypeTag[A] =
    makeTypeTag(term.typeSignature, runtimeMirror)

  /** makes a type tag for a type */
  def makeTypeTag[A](tpe: Type, mirror: RuntimeMirror): TypeTag[A] = {
    val typeCreator = new TypeCreator {
      def apply[U <: Universe with Singleton](m: Mirror[U]): U # Type =
        tpe.asInstanceOf[U # Type]
    }
    TypeTag[A](mirror, typeCreator)
  }

  /** makes a class tag for a type tag */
  def typeTagToClassTag[T : TypeTag]: ClassTag[T] = {
    ClassTag[T](typeTag[T].mirror.runtimeClass(typeTag[T].tpe))
  }

  /** given an instance that contains an inner module, and the name of the
   * inner module, return the inner module instance wrapped in a `Some`. returns
   * `None` if there is no such inner module.
   *
   * @param container the instance that contains the inner module
   * @param moduleName the name of the inner module
   * @return the inner module instance, if it exists
   */
  def innerModule(container: Any, moduleName: String): Option[Any] = {
    val containerMirror = scala.reflect.runtime.universe.runtimeMirror(container.getClass.getClassLoader)
    val instanceMirror: InstanceMirror = containerMirror.reflect(container)
    if (instanceMirror.symbol.isStatic) {
      try {
        val symbol: ModuleSymbol = containerMirror.staticModule(s"${instanceMirror.symbol.fullName}.$moduleName")
        val mirror: ModuleMirror = containerMirror.reflectModule(symbol)
        Some(mirror.instance)
      } catch {
        case e: ScalaReflectionException => None
      }
    } else {
      val symbol: Symbol = instanceMirror.symbol.selfType.decl(TermName(s"$moduleName$$"))
      if (!symbol.isModule) {
        None
      }
      else {
        val mirror: ModuleMirror = instanceMirror.reflectModule(symbol.asModule)
        Some(mirror.instance)
      }
    }
  }

  /** given an instance and a type, returns a set of all the `val` or `var`
   * members of the instance that match the type.
   *
   * @tparam A the type of the terms we are searching for
   * @param instance the instance to search for terms with matching type
   */
  def termsWithType[A : TypeKey](instance: Any): Seq[A] = {
    val runtimeMirror =  scala.reflect.runtime.universe.runtimeMirror(instance.getClass.getClassLoader)
    val instanceMirror: InstanceMirror = runtimeMirror.reflect(instance)
    val symbols = instanceMirror.symbol.selfType.decls.toSeq
    val termSymbols = symbols.collect {
      case s if s.isTerm => s.asTerm
    }
    val valOrVarSymbols = termSymbols.filter {
      s => s.isVal || s.isVar
    }
    val matchingSymbols = valOrVarSymbols.filter { symbol =>
      val tpe: Type = symbol.typeSignature
      tpe <:< typeKey[A].tpe
    }
    matchingSymbols.map { symbol =>
      val fieldMirror = instanceMirror.reflectField(symbol)
      fieldMirror.get.asInstanceOf[A]
    }
  }

}
