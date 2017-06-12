package longevity.emblem.emblematic.factories

import typekey.TypeKey
import longevity.emblem.emblematic.Emblem
import longevity.emblem.emblematic.EmblemProp
import longevity.emblem.emblematic.Union
import longevity.emblem.emblematic.UnionConstituentLookup
import longevity.emblem.emblematic.UnionProp
import longevity.emblem.exceptions.ConstituentPropTypeMismatchException
import longevity.emblem.exceptions.InstanceNotInUnionException
import longevity.emblem.reflectionUtil.TypeReflector
import longevity.emblem.reflectionUtil.makeTypeTag
import typekey.typeKey
import scala.reflect.runtime.universe.TermName
import scala.reflect.runtime.universe.Type

/** generates an [[Union]] from the corresponding [[TypeKey]] */
private[emblem] class UnionFactory[A : TypeKey] extends TypeReflector[A] {

  /** generates the union */
  def generate(constituents: Set[Emblem[_ <: A]]): Union[A] = {
    val lookup = new UnionConstituentLookup[A](constituents)
    val unionProps = abstractPublicVals.map { publicVal =>
      unionProp(publicVal.name, lookup)
    }
    Union[A](
      key,
      constituents,
      unionProps,
      lookup)
  }

  private def unionProp(
    name: TermName,
    lookup: UnionConstituentLookup[A])
  : UnionProp[A, _] = {
    val propType: Type = tpe.member(name).asTerm.typeSignature.resultType

    // the Any here is bogus. it comes back as something else
    val propTypeTag = makeTypeTag[Any](propType, tag.mirror)

    val propKey = TypeKey(propTypeTag)
    assertConstituentsHaveSupportingProp(name.toString, propKey, lookup)
    makeUnionProp(name, lookup)(propKey)
  }

  private def assertConstituentsHaveSupportingProp(
    name: String,
    propKey: TypeKey[_],
    lookup: UnionConstituentLookup[A])
  : Unit = {
    lookup.constituents.foreach { emblem =>
      if (! (emblem(name).typeKey =:= propKey)) {
        throw new ConstituentPropTypeMismatchException(
          typeKey[A],
          name,
          propKey,
          emblem.typeKey,
          emblem(name).typeKey)
      } 
    }
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
      val emblem = lookup.emblemForInstance(a).getOrElse {
        throw new InstanceNotInUnionException(a, typeKey[A])
      }
      emblem.propsMap(name).asInstanceOf[EmblemProp[A, B]].set(a, b)
    }
  }

}
