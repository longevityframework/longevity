package emblem

import scala.reflect.runtime.universe._
import org.scalatest._

/** [[TypeKey type key]] specifications */
class TypeKeySpec extends FlatSpec with GivenWhenThen with Matchers {

  // TODO pt-86950678: spec showing when TypeKeys are equal where TypeTags are not

  behavior of "the TypeKey constructor"
  it should "produce a valid type key from a type tag" in {
    val tag = typeTag[List[Int]]
    val key = TypeKey(tag)
    key.tag should equal (tag)
    key.tpe should equal (tag.tpe)
  }

  behavior of "method emblem.typeKey"
  it should "produce a valid type key from a type argument" in {
    val tag = typeTag[List[Int]]
    val key = typeKey[List[Int]]
    key.tag should equal (tag)
    key.tpe should equal (tag.tpe)
  }

  behavior of "method emblem.typeKeyFromTag"
  it should "produce a valid type key from a type argument" in {
    def foo[A : TypeKey](): Unit = {
      val tag = typeTag[List[Int]]
      val key = implicitly[TypeKey[A]]
      key.tag should equal (tag)
      key.tpe should equal (tag.tpe)
    }
    foo[List[Int]]
  }

  behavior of "TypeKey equals and hashCode"
  it should "treat two types as equal whenever the underlying types are equivalent according to Type.=:=" in {
    val key1 = typeKey[List[Int]]
    val key2 = typeKey[List[Int]]
    val key3 = typeKey[List[String]]
    key1 should equal (key2)
    key1.hashCode should equal (key2.hashCode)
    key1 should not equal (key3)
    // key1 and key3 hashCodes are not guaranteed to differ!
  }

  behavior of "TypeKey.typeArgs"
  it should "return a list of TypeKeys representing the type arguments of the type" in {
    typeKey[Int].typeArgs should equal (List.empty)
    typeKey[List[_]].typeArgs should equal (List(typeKey[Any]))
    typeKey[List[Int]].typeArgs should equal (List(typeKey[Int]))
    typeKey[Map[String, Int]].typeArgs should equal (List(typeKey[String], typeKey[Int]))
  }

  behavior of "TypeKey.castToLowerBound"
  it should "present the same equality, but different typing, for the typekey, when the new typing is safe" in {
    val tki = typeKey[Int]
    val tkli = typeKey[List[Int]]
    val tkla = typeKey[List[_]]
    val tksi = typeKey[Set[Int]]
    val keys: List[TypeKey[_]] = List(tki, tkli, tkla, tksi)

    // this is how i type:

    val lotks: List[Option[TypeKey[_ >: String]]] = keys.map(_.castToLowerBound[String])
    val lotki: List[Option[TypeKey[_ >: Int]]] = keys.map(_.castToLowerBound[Int])

    val lotkli: List[Option[TypeKey[_ >: List[Int]]]] = keys.map(_.castToLowerBound[List[Int]])
    val lotkla: List[Option[TypeKey[_ >: List[_]]]] = keys.map(_.castToLowerBound[List[_]])

    // this is how i equal:

    keys.map(_.castToLowerBound[String]) should equal (List(None, None, None, None))
    keys.map(_.castToLowerBound[Int]) should equal (List(Some(tki), None, None, None))

    keys.map(_.castToLowerBound[List[Int]]) should equal (List(None, Some(tkli), Some(tkla), None))
    keys.map(_.castToLowerBound[List[_]]) should equal (List(None, None, Some(tkla), None))
  }

  behavior of "TypeKey.castToUpperBound"
  it should "present the same equality, but different typing, for the typekey, when the new typing is safe" in {
    val tki = typeKey[Int]
    val tkli = typeKey[List[Int]]
    val tkla = typeKey[List[_]]
    val tksi = typeKey[Set[Int]]
    val keys: List[TypeKey[_]] = List(tki, tkli, tkla, tksi)

    // this is how i type:

    val lotks: List[Option[TypeKey[_ <: String]]] = keys.map(_.castToUpperBound[String])
    val lotki: List[Option[TypeKey[_ <: Int]]] = keys.map(_.castToUpperBound[Int])

    val lotkli: List[Option[TypeKey[_ <: List[Int]]]] = keys.map(_.castToUpperBound[List[Int]])
    val lotkla: List[Option[TypeKey[_ <: List[_]]]] = keys.map(_.castToUpperBound[List[_]])

    // this is how i equal:

    keys.map(_.castToUpperBound[String]) should equal (List(None, None, None, None))
    keys.map(_.castToUpperBound[Int]) should equal (List(Some(tki), None, None, None))

    keys.map(_.castToUpperBound[List[Int]]) should equal (List(None, Some(tkli), None, None))
    keys.map(_.castToUpperBound[List[_]]) should equal (List(None, Some(tkli), Some(tkla), None))
  }

}
