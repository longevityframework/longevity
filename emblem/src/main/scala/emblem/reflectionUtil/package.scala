package emblem
 
import scala.reflect.ClassTag
import scala.reflect.api.Mirror
import scala.reflect.api.TypeCreator
import scala.reflect.api.Universe
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe.typeTag
import scala.reflect.runtime.universe.Symbol
import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TypeTag

/** generally useful utility functions for working with Scala reflection library */
package object reflectionUtil {

  // overloaded makeTypeTag follows FixedMirrorTypeCreator in
  // https://github.com/scala/scala/blob/2.11.x/src/reflect/scala/reflect/internal/StdCreators.scala

  /** makes a type tag for a term */
  def makeTypeTag[A](term: Symbol): TypeTag[A] = makeTypeTag(term.typeSignature)

  /** makes a type tag for a type */
  def makeTypeTag[A](tpe: Type): TypeTag[A] = {
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

  /** makes a class tag for a type tag */
  def typeTagToClassTag[T : TypeTag]: ClassTag[T] = {
    ClassTag[T](typeTag[T].mirror.runtimeClass(typeTag[T].tpe))
  }

  /** produces a function that, give an instance of type `A`, produces a set of
   * all the members of that instance that match type `B`
   */
  protected def membersWithType[A : TypeTag, B : TypeTag]: (A) => Set[B] = {
    val tag = implicitly[TypeTag[A]]
    val tpe = tag.tpe
    val terms = tpe.members.collect {
      case symbol if symbol.isTerm => symbol.asTerm
    }
    val valOrVarGetters = terms.collect {
      case term if term.isVal || term.isVar => term.getter
    }
    
    ???
  }

}
