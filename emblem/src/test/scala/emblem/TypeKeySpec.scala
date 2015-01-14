package emblem

import scala.reflect.runtime.universe._
import org.scalatest._
import org.scalatest.OptionValues._

/** [[TypeKey type key]] specifications */
class TypeKeySpec extends FlatSpec with GivenWhenThen with Matchers {

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
    val key = emblem.typeKey[List[Int]]
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

  behavior of "equals and hashCode"
  it should "treat two types as equal whenever the underlying types are equivalent according to Type.=:=" in {
    val key1 = typeKey[List[Int]]
    val key2 = typeKey[List[Int]]
    val key3 = typeKey[List[String]]
    key1 should equal (key2)
    key1.hashCode should equal (key2.hashCode)
    key1 should not equal (key3)
    // key1 and key2 hashCodes are not guaranteed to differ!
  }

}
