package longevity.emblem.emblematic

import typekey.typeKey
import longevity.emblem.exceptions.ConstituentPropTypeMismatchException
import longevity.emblem.exceptions.NoSuchPropertyException
import longevity.emblem.testData.exhaustive.TraitWithAbstractProp
import longevity.emblem.testData.exhaustive.traitWithAbstractPropUnion
import longevity.emblem.testData.exhaustive.traitWithConcretePropUnion
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** contains an example of when a constituent has a type mismatch with the parent type */
object UnionSpec {

  trait Foo { val i: Int }
  case class Bar(i: Int) extends Foo
  case class Baz(i: Int) extends Foo

  trait HasFoo { val foo: Foo }
  case class HasBar(foo: Bar) extends HasFoo // << this type specialization is problematic
  case class HasBaz(foo: Baz) extends HasFoo // << this type specialization is problematic

}

/** [[Union union]] specifications */
class UnionSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "Union.apply"

  it should "throw exception when a constituent has a type mismatch with the parent type" in {

    // note that because the union type is superclass to the constituent type,
    // the only way that the types can be less than an exact match is if the
    // constituent property is a subtype of the union property. note that this
    // would be perfectly fine in view of `ReflectiveProp.get`, but is quite
    // problematic in the case of `ReflectiveProp.set`.

    import UnionSpec._

    val barEmblem = Emblem[Bar]
    val bazEmblem = Emblem[Baz]
    val fooUnion = Union[Foo](barEmblem, bazEmblem)

    val hasBarEmblem = Emblem[HasBar]
    val hasBazEmblem = Emblem[HasBaz]

    intercept[ConstituentPropTypeMismatchException] {
      val hasFooUnion = Union[HasFoo](hasBarEmblem, hasBazEmblem)
    }

  }

  behavior of "a union"

  it should "retain name information" in {
    traitWithAbstractPropUnion.namePrefix should equal (
      "longevity.emblem.testData.exhaustive")
    traitWithAbstractPropUnion.name should equal (
      "TraitWithAbstractProp")
    traitWithAbstractPropUnion.fullname should equal (
      "longevity.emblem.testData.exhaustive.TraitWithAbstractProp")
  }

  it should "retain type information" in {
    traitWithAbstractPropUnion.typeKey should equal (typeKey[TraitWithAbstractProp])
  }

  it should "dump helpful debug info" in {
    traitWithAbstractPropUnion.debugInfo should equal (
      """|longevity.emblem.testData.exhaustive.TraitWithAbstractProp {
         |  common: String
         |}""".stripMargin)
  }

  behavior of "Union.props"

  it should "produce props for abstract public vals" in {
    traitWithAbstractPropUnion.props.size should equal (1)
    traitWithConcretePropUnion.props.size should equal (0)
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
