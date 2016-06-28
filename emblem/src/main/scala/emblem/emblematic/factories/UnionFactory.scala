package emblem.emblematic.factories

import emblem.emblematic.Emblem
import emblem.emblematic.EmblemProp
import emblem.emblematic.Union
import emblem.emblematic.UnionConstituentLookup
import emblem.emblematic.UnionProp
import emblem.TypeKey
import emblem.reflectionUtil.makeTypeTag
import emblem.reflectionUtil.TypeReflector
import scala.reflect.runtime.universe.Type
import scala.reflect.runtime.universe.TermName

/** generates an [[Union]] from the corresponding [[TypeKey]] */
private[emblem] class UnionFactory[A : TypeKey] extends TypeReflector[A] {

  /** generates the union */
  def generate(constituents: Set[Emblem[_ <: A]]): Union[A] = {
    val lookup = new UnionConstituentLookup[A](constituents)
    val unionProps = publicVals.map { publicVal =>
      unionProp(publicVal.name, lookup)
    }
    Union[A](
      key,
      constituents,
      unionProps)
  }

  private def unionProp(
    name: TermName,
    lookup: UnionConstituentLookup[A])
  : UnionProp[A, _] = {
    val propType: Type = tpe.member(name).asTerm.typeSignature.resultType
    val propTypeTag = makeTypeTag[Any](propType) // the Any here is bogus. it comes back as something else
    val propKey = TypeKey(propTypeTag)
    makeUnionProp(name, lookup)(propKey)
  }

  private def makeUnionProp[B](
    termName: TermName,
    lookup: UnionConstituentLookup[A])(
    implicit propKey: TypeKey[B])
  : UnionProp[A, B] = {
    val name = termName.toString
    UnionProp[A, B](
      name,
      getFunction[B](termName),
      setFunction[B](name, lookup))(
      propKey)
  }

  private def setFunction[B : TypeKey](
    name: String,
    lookup: UnionConstituentLookup[A])
  : (A, B) => A = {
    { (a: A, b: B) =>
      val emblem = lookup.emblemForInstance(a).get // TODO get??
      // TODO is this asInstanceOf correct?
      emblem.propsMap(name).asInstanceOf[EmblemProp[A, B]].set(a, b)
    }
  }

}
