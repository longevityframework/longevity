package emblem

import org.scalatest._
import org.scalatest.OptionValues._

/** [[Emblem emblem]] specifications */
class EmblemSpec extends FlatSpec with GivenWhenThen with Matchers {

  private val pointEmblem = emblemFor[Point]

  behavior of "an emblem"

  it should "retain name information" in {
    pointEmblem.namePrefix should equal ("emblem")
    pointEmblem.name should equal ("Point")
    pointEmblem.fullname should equal ("emblem.Point")
  }

  it should "retain type information" in {
    pointEmblem.typeKey should equal (typeKey[Point])
  }

  it should "dump helpful debug info" in {
    pointEmblem.debugInfo should equal (
      """|emblem.Point {
         |  x: scala.Double
         |  y: scala.Double
         |}""".stripMargin)
  }

  behavior of "Emblem.apply"

  it should "return untyped properties" in {
    val xProp = pointEmblem("x")
    xProp shouldBe a [EmblemProp[Point, _]]
  }

  it should "throw exception when no such property" in {
    intercept[NoSuchElementException] { pointEmblem("z") }
  }

  behavior of "Emblem.prop"

  it should "return typed properties" in {
    val xProp: EmblemProp[Point, Double] = pointEmblem.prop[Double]("x")
  }

  it should "throw NoSuchElementException when no such property" in {
    intercept[NoSuchElementException] { pointEmblem.prop[Double]("z") }
  }

  it should "throw ClassCastException when property type does not match" in {
    intercept[ClassCastException] { pointEmblem.prop[String]("x") }
  }

}
