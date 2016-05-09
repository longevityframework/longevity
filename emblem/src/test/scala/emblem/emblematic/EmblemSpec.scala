package emblem.emblematic

import emblem.exceptions.NoSuchPropertyException
import emblem.testData.exhaustive.WithBasics
import emblem.testData.exhaustive.WithCollections
import emblem.testData.exhaustive.WithExtractors
import emblem.testData.exhaustive.withBasicsEmblem
import emblem.testData.exhaustive.withCollectionsEmblem
import emblem.testData.exhaustive.withExtractorsEmblem
import emblem.testData.geometry.Point
import emblem.testData.geometry.pointEmblem
import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** [[Emblem emblem]] specifications */
class EmblemSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "an emblem"

  it should "retain name information" in {
    pointEmblem.namePrefix should equal ("emblem.testData.geometry")
    pointEmblem.name should equal ("Point")
    pointEmblem.fullname should equal ("emblem.testData.geometry.Point")

    withBasicsEmblem.namePrefix should equal ("emblem.testData.exhaustive")
    withBasicsEmblem.name should equal ("WithBasics")
    withBasicsEmblem.fullname should equal ("emblem.testData.exhaustive.WithBasics")
  }

  it should "retain type information" in {
    pointEmblem.typeKey should equal (typeKey[Point])
  }

  it should "dump helpful debug info" in {
    pointEmblem.debugInfo should equal (
      """|emblem.testData.geometry.Point {
         |  x: scala.Double
         |  y: scala.Double
         |}""".stripMargin)
  }

  behavior of "Emblem.props"

  it should "be the right size" in {
    pointEmblem.props.size should equal (2)
    withBasicsEmblem.props.size should equal (8)
    withExtractorsEmblem.props.size should equal (3)
    withCollectionsEmblem.props.size should equal (3)
  }

  behavior of "Emblem.apply(String)"

  it should "return untyped properties" in {
    val xProp = pointEmblem("x")
    xProp shouldBe a [EmblemProp[Point, _]]
  }

  it should "throw exception when no such property" in {
    intercept[NoSuchPropertyException] { pointEmblem("z") }
  }

  behavior of "Emblem.prop"

  it should "return typed properties" in {
    val xProp: EmblemProp[Point, Double] = pointEmblem.prop[Double]("x")
  }

  it should "throw NoSuchPropertyException when no such property" in {
    intercept[NoSuchPropertyException] { pointEmblem.prop[Double]("z") }
  }

  it should "throw ClassCastException when property type does not match" in {
    intercept[ClassCastException] { pointEmblem.prop[String]("x") }
  }

}
