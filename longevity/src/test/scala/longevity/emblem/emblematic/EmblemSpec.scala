package longevity.emblem.emblematic

import longevity.emblem.exceptions.NoSuchPropertyException
import longevity.emblem.testData.exhaustive.withBasicsEmblem
import longevity.emblem.testData.exhaustive.withCollectionsEmblem
import longevity.emblem.testData.exhaustive.withSinglePropEmblemsEmblem
import longevity.emblem.testData.geometry.Point
import longevity.emblem.testData.geometry.pointEmblem
import longevity.emblem.testData.geometry.pointWithDefaultsEmblem
import typekey.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** [[Emblem emblem]] specifications */
class EmblemSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "an emblem"

  it should "retain name information" in {
    pointEmblem.namePrefix should equal ("longevity.emblem.testData.geometry")
    pointEmblem.name should equal ("Point")
    pointEmblem.fullname should equal ("longevity.emblem.testData.geometry.Point")

    pointWithDefaultsEmblem.namePrefix should equal ("longevity.emblem.testData.geometry")
    pointWithDefaultsEmblem.name should equal ("PointWithDefaults")
    pointWithDefaultsEmblem.fullname should equal ("longevity.emblem.testData.geometry.PointWithDefaults")

    withBasicsEmblem.namePrefix should equal ("longevity.emblem.testData.exhaustive")
    withBasicsEmblem.name should equal ("WithBasics")
    withBasicsEmblem.fullname should equal ("longevity.emblem.testData.exhaustive.WithBasics")
  }

  it should "retain type information" in {
    pointEmblem.typeKey should equal (typeKey[Point])
  }

  it should "dump helpful debug info" in {
    pointEmblem.debugInfo should equal (
      """|longevity.emblem.testData.geometry.Point {
         |  x: Double
         |  y: Double
         |}""".stripMargin)
  }

  behavior of "Emblem.props"

  it should "be the right size" in {
    pointEmblem.props.size should equal (2)
    pointWithDefaultsEmblem.props.size should equal (2)
    withBasicsEmblem.props.size should equal (8)
    withSinglePropEmblemsEmblem.props.size should equal (3)
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
