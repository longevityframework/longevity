package emblem.emblematic

import emblem.typeKey
import emblem.exceptions.NoSuchPropertyException
import emblem.testData.exhaustive.TraitWithAbstractProp
import emblem.testData.exhaustive.traitWithAbstractPropUnion
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** [[Union union]] specifications */
class UnionSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "a union"

  it should "retain name information" in {
    traitWithAbstractPropUnion.namePrefix should equal (
      "emblem.testData.exhaustive")
    traitWithAbstractPropUnion.name should equal (
      "TraitWithAbstractProp")
    traitWithAbstractPropUnion.fullname should equal (
      "emblem.testData.exhaustive.TraitWithAbstractProp")
  }

  it should "retain type information" in {
    traitWithAbstractPropUnion.typeKey should equal (typeKey[TraitWithAbstractProp])
  }

  it should "dump helpful debug info" in {
    traitWithAbstractPropUnion.debugInfo should equal (
      """|emblem.testData.exhaustive.TraitWithAbstractProp {
         |  common: String
         |}""".stripMargin)
  }

  behavior of "Union.props"

  it should "be the right size" in {
    traitWithAbstractPropUnion.props.size should equal (1)
  }

  behavior of "Union.apply(String)"

  it should "return untyped properties" in {
    val p: UnionProp[TraitWithAbstractProp, _] = traitWithAbstractPropUnion("common")
  }

  it should "throw exception when no such property" in {
    intercept[NoSuchPropertyException] { traitWithAbstractPropUnion("extraordinary") }
  }

  behavior of "Union.prop"

  it should "return typed properties" in {
    val p: UnionProp[TraitWithAbstractProp, String] = traitWithAbstractPropUnion.prop[String]("common")
  }

  it should "throw NoSuchPropertyException when no such property" in {
    intercept[NoSuchPropertyException] { traitWithAbstractPropUnion.prop[String]("extraordinary") }
  }

  it should "throw ClassCastException when property type does not match" in {
    intercept[ClassCastException] { traitWithAbstractPropUnion.prop[Int]("common") }
  }

}
