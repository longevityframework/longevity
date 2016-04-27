package emblem.factories

import emblem.Emblem
import emblem.Union
import emblem.UnionProp
import emblem.TypeKey
import emblem.reflectionUtil.makeTypeTag
import emblem.reflectionUtil.TypeReflector
import scala.reflect.ClassTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe.NoSymbol
import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TermName

/** generates an [[Union]] from the corresponding [[TypeKey]] */
private[emblem] class UnionFactory[A : TypeKey] extends TypeReflector[A] {

  /** generates the union */
  def generate(constituents: Set[Emblem[_ <: A]]): Union[A] = Union[A](
    key,
    constituents,
    publicVals.map(_.name).map(unionProp(_)))

  private def unionProp(name: TermName): UnionProp[A, _] = {
    val propType: Type = tpe.member(name).asTerm.typeSignature.resultType
    val propTypeTag = makeTypeTag[Any](propType) // the Any here is bogus. it comes back as something else
    val propKey = TypeKey(propTypeTag)
    makeUnionProp(name)(propKey)
  }

  private def makeUnionProp[B](name: TermName)(implicit propKey: TypeKey[B]): UnionProp[A, B] =
    UnionProp[A, B](
      name.toString,
      getFunction[B](name))(
      propKey)

}
