package emblem.emblematic

import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[emblem.emblematic.basicTypes]] */
class BasicTypesSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "emblem.emblematic.basicTypes.basicTypeKeys"
  it should "contain type keys for the basic types, and nothing else" in {

    basicTypes.basicTypeKeys.contains(typeKey[Boolean]) should be (true)
    basicTypes.basicTypeKeys.contains(typeKey[Char]) should be (true)
    basicTypes.basicTypeKeys.contains(typeKey[org.joda.time.DateTime]) should be (true)
    basicTypes.basicTypeKeys.contains(typeKey[Double]) should be (true)
    basicTypes.basicTypeKeys.contains(typeKey[Float]) should be (true)
    basicTypes.basicTypeKeys.contains(typeKey[Int]) should be (true)
    basicTypes.basicTypeKeys.contains(typeKey[Long]) should be (true)
    basicTypes.basicTypeKeys.contains(typeKey[String]) should be (true)

    // this size check asserts the "nothing else" part of the spec
    basicTypes.basicTypeKeys.size should equal (8)
  }

  behavior of "emblem.emblematic.basicTypes.isBasicType"
  it should "return true for type keys for the basic types, and false for any other type" in {

    basicTypes.isBasicType[Boolean] should be (true)
    basicTypes.isBasicType[Char] should be (true)
    basicTypes.isBasicType[org.joda.time.DateTime] should be (true)
    basicTypes.isBasicType[Double] should be (true)
    basicTypes.isBasicType[Float] should be (true)
    basicTypes.isBasicType[Int] should be (true)
    basicTypes.isBasicType[Long] should be (true)
    basicTypes.isBasicType[String] should be (true)

    basicTypes.isBasicType[Nothing] should be (false)
    basicTypes.isBasicType[Any] should be (false)
    basicTypes.isBasicType[AnyRef] should be (false)
    basicTypes.isBasicType[List[_]] should be (false)
    basicTypes.isBasicType[Some[_]] should be (false)
    basicTypes.isBasicType[Option[Nothing]] should be (false)
  }

}
