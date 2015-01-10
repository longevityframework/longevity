package emblem

import scala.reflect.runtime.universe.typeTag
import org.scalatest._
import org.scalatest.OptionValues._

/** some basic tests for type tag maps */
class TypeTagMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TypeTagMap()"
  it should "produce an empty type tag map" in {
    val map = TypeTagMap()
    map.isEmpty should equal (true)
    map.size should equal (0)
    map.get[Int] should equal (None)
    intercept[NoSuchElementException] { map[Int] }
  }

  behavior of "TypeTagMap(value)"
  it should "produce a type tag map with a single mapping" in {
    val map = TypeTagMap(List[Int]())
    map.isEmpty should equal (false)
    map.size should equal (1)
    map.get[Int].value should equal (List[Int]())
    map[Int] should equal (List[Int]())
    map.get[Double] should equal (None)
    intercept[NoSuchElementException] { map[Double] }
  }

  behavior of "TypeTagMap(tag -> value)"
  it should "produce a type tag map with a single mapping" in {
    val map = TypeTagMap(typeTag[Int] -> List[Int]())
    map.isEmpty should equal (false)
    map.size should equal (1)
    map.get[Int].value should equal (List[Int]())
    map[Int] should equal (List[Int]())
    map.get[Double] should equal (None)
    intercept[NoSuchElementException] { map[Double] }
  }

  behavior of "TypeTagMap(value1, value2)"
  it should "produce a type tag map with two mappings" in {
    val intList = List(1, 2, 3)
    val floatList = List(0.0f, 1.1f)
    val map = TypeTagMap(intList, floatList)
    map.isEmpty should equal (false)
    map.size should equal (2)
    map.get[Int].value should equal (intList)
    map[Int] should equal (intList)
    map.get[Float].value should equal (floatList)
    map[Float] should equal (floatList)
    map.get[Double] should equal (None)
    intercept[NoSuchElementException] { map[Double] }
  }

  behavior of "TypeTagMap.+(value)"
  it should "produce a type tag map with an added mapping" in {
    val intList = List(1, 2, 3)
    val floatList = List(0.0f, 1.1f)
    val map0 = TypeTagMap.empty

    val map1 = map0 + intList
    map1.isEmpty should equal (false)
    map1.get[Int].value should equal (intList)
    map1[Int] should equal (intList)
    map1.get[Double] should equal (None)
    intercept[NoSuchElementException] { map1[Double] }

    val map2 = map1 + floatList
    map2.isEmpty should equal (false)
    map2.get[Int].value should equal (intList)
    map2[Int] should equal (intList)
    map2.get[Float].value should equal (floatList)
    map2[Float] should equal (floatList)
    map2.get[Double] should equal (None)
    intercept[NoSuchElementException] { map2[Double] }
  }

}
