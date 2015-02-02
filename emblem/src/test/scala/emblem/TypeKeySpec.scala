package emblem

import scala.reflect.runtime.universe._
import org.scalatest._
import org.scalatest.OptionValues._

/** [[TypeKey type key]] specifications */
class TypeKeySpec extends FlatSpec with GivenWhenThen with Matchers {

  // TODO: spec showing when TypeKeys are equal where TypeTags are not

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

}
