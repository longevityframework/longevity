package emblem
 
import scala.reflect.api.Mirror
import scala.reflect.api.TypeCreator
import scala.reflect.api.Universe
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe.TermSymbol
import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TypeTag

/** generally useful utility functions for working with Scala reflection library */
private[emblem] object reflectionUtil {

  // overloaded makeTypeTag follows FixedMirrorTypeCreator in
  // https://github.com/scala/scala/blob/2.11.x/src/reflect/scala/reflect/internal/StdCreators.scala

  def makeTypeTag[A](term: TermSymbol): TypeTag[A] = makeTypeTag(term.typeSignature)

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

}
