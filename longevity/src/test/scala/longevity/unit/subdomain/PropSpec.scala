package longevity.unit.subdomain

import com.github.nscala_time.time.Imports._
import emblem.imports._
import longevity.exceptions.subdomain._
import org.scalatest._
import longevity.subdomain._

/** unit tests for the proper construction of [[RootEntityType#Prop nat key props]] */
class PropSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "RootEntityType.Prop.apply(String)"

  it should "throw exception when the specified prop path is empty" in {
    import longevity.integration.subdomain.allAttributes._
    intercept[EmptyPropPathException] {
      AllAttributes.prop[Int]("")
    }
  }

  it should "throw exception when the specified prop path does not map to an actual prop path" in {
    import longevity.integration.subdomain.allAttributes._
    intercept[NoSuchPropPathSegmentException] {
      AllAttributes.prop[Int]("invalidPropPath")
    }

    import longevity.integration.subdomain.withComponent._
    intercept[NoSuchPropPathSegmentException] {
      WithComponent.prop[Int]("component.noSuchPathSegment")
    }
  }

  it should "throw exception when the specified prop path passes through a collection" in {
    import longevity.integration.subdomain.withComponentList._
    intercept[NonEntityPropPathSegmentException] {
      WithComponentList.prop[Int]("components.uri")
    }

    import longevity.integration.subdomain.withComponentOption._
    intercept[NonEntityPropPathSegmentException] {
      WithComponentOption.prop[Int]("component.uri")
    }

    import longevity.integration.subdomain.withComponentSet._
    intercept[NonEntityPropPathSegmentException] {
      WithComponentSet.prop[Int]("components.uri")
    }
  }

  it should "throw exception when the specified prop path terminates with a collection" in {
    import longevity.integration.subdomain.attributeLists._
    intercept[InvalidPropPathLeafException] {
      AttributeLists.prop[Int]("boolean")
    }

    import longevity.integration.subdomain.attributeOptions._
    intercept[InvalidPropPathLeafException] {
      AttributeOptions.prop[Int]("boolean")
    }
 
    import longevity.integration.subdomain.attributeSets._
    intercept[InvalidPropPathLeafException] {
      AttributeSets.prop[Int]("boolean")
    }

    import longevity.integration.subdomain.withAssocList._
    intercept[InvalidPropPathLeafException] {
      WithAssocList.prop[Int]("associated")
    }

    import longevity.integration.subdomain.withAssocOption._
    intercept[InvalidPropPathLeafException] {
      WithAssocOption.prop[Int]("associated")
    }

    import longevity.integration.subdomain.withAssocSet._
    intercept[InvalidPropPathLeafException] {
      WithAssocSet.prop[Int]("associated")
    }

    import longevity.integration.subdomain.withComponentList._
    intercept[InvalidPropPathLeafException] {
      WithComponentList.prop[Int]("components")
    }

    import longevity.integration.subdomain.withComponentOption._
    intercept[InvalidPropPathLeafException] {
      WithComponentOption.prop[Int]("component")
    }

    import longevity.integration.subdomain.withComponentSet._
    intercept[InvalidPropPathLeafException] {
      WithComponentSet.prop[Int]("components")
    }

    import longevity.integration.subdomain.withComponent._
    intercept[InvalidPropPathLeafException] {
      WithComponent.prop[Int]("component.tags")
    }
  }

  // TODO test for type mismatch

  it should "produce a valid nat key prop for basic types" in {
    import longevity.integration.subdomain.allAttributes._

    var prop: Prop[AllAttributes, _] = null

    prop = AllAttributes.prop[Boolean]("boolean")
    prop.path should equal ("boolean")
    prop.typeKey should equal (typeKey[Boolean])

    prop = AllAttributes.prop[Char]("char")
    prop.path should equal ("char")
    prop.typeKey should equal (typeKey[Char])

    prop = AllAttributes.prop[Double]("double")
    prop.path should equal ("double")
    prop.typeKey should equal (typeKey[Double])

    prop = AllAttributes.prop[Float]("float")
    prop.path should equal ("float")
    prop.typeKey should equal (typeKey[Float])

    prop = AllAttributes.prop[Int]("int")
    prop.path should equal ("int")
    prop.typeKey should equal (typeKey[Int])

    prop = AllAttributes.prop[Long]("long")
    prop.path should equal ("long")
    prop.typeKey should equal (typeKey[Long])

    prop = AllAttributes.prop[String]("string")
    prop.path should equal ("string")
    prop.typeKey should equal (typeKey[String])

    prop = AllAttributes.prop[DateTime]("dateTime")
    prop.path should equal ("dateTime")
    prop.typeKey should equal (typeKey[DateTime])
  }

  it should "produce a valid nat key prop for shorthand types" in {
    import longevity.integration.subdomain.allShorthands._

    var prop: Prop[AllShorthands, _] = null

    prop = AllShorthands.prop[BooleanShorthand]("boolean")
    prop.path should equal ("boolean")
    prop.typeKey should equal (typeKey[BooleanShorthand])

    prop = AllShorthands.prop[CharShorthand]("char")
    prop.path should equal ("char")
    prop.typeKey should equal (typeKey[CharShorthand])

    prop = AllShorthands.prop[DoubleShorthand]("double")
    prop.path should equal ("double")
    prop.typeKey should equal (typeKey[DoubleShorthand])

    prop = AllShorthands.prop[FloatShorthand]("float")
    prop.path should equal ("float")
    prop.typeKey should equal (typeKey[FloatShorthand])

    prop = AllShorthands.prop[IntShorthand]("int")
    prop.path should equal ("int")
    prop.typeKey should equal (typeKey[IntShorthand])

    prop = AllShorthands.prop[LongShorthand]("long")
    prop.path should equal ("long")
    prop.typeKey should equal (typeKey[LongShorthand])

    prop = AllShorthands.prop[StringShorthand]("string")
    prop.path should equal ("string")
    prop.typeKey should equal (typeKey[StringShorthand])

    prop = AllShorthands.prop[DateTimeShorthand]("dateTime")
    prop.path should equal ("dateTime")
    prop.typeKey should equal (typeKey[DateTimeShorthand])
  }

  it should "produce a valid nat key prop for an assoc" in {
    import longevity.integration.subdomain.withAssoc._
    val prop = WithAssoc.prop[Assoc[Associated]]("associated")
    prop.path should equal ("associated")
    prop.typeKey should equal (typeKey[Assoc[Associated]])
  }

  it should "produce a valid nat key prop for a nested basic type" in {
    import longevity.integration.subdomain.withComponent._
    val prop = WithComponent.prop[String]("component.uri")
    prop.path should equal ("component.uri")
    prop.typeKey should equal (typeKey[String])
  }

  it should "produce a valid nat key prop for shorthand types in nested components" in {
    import longevity.integration.subdomain.withComponentWithShorthands._
    var prop: Prop[WithComponentWithShorthands, _] = null

    prop = WithComponentWithShorthands.prop[BooleanShorthand]("component.boolean")
    prop.path should equal ("component.boolean")
    prop.typeKey should equal (typeKey[BooleanShorthand])

    prop = WithComponentWithShorthands.prop[CharShorthand]("component.char")
    prop.path should equal ("component.char")
    prop.typeKey should equal (typeKey[CharShorthand])

    prop = WithComponentWithShorthands.prop[DoubleShorthand]("component.double")
    prop.path should equal ("component.double")
    prop.typeKey should equal (typeKey[DoubleShorthand])

    prop = WithComponentWithShorthands.prop[FloatShorthand]("component.float")
    prop.path should equal ("component.float")
    prop.typeKey should equal (typeKey[FloatShorthand])

    prop = WithComponentWithShorthands.prop[IntShorthand]("component.int")
    prop.path should equal ("component.int")
    prop.typeKey should equal (typeKey[IntShorthand])

    prop = WithComponentWithShorthands.prop[LongShorthand]("component.long")
    prop.path should equal ("component.long")
    prop.typeKey should equal (typeKey[LongShorthand])

    prop = WithComponentWithShorthands.prop[StringShorthand]("component.string")
    prop.path should equal ("component.string")
    prop.typeKey should equal (typeKey[StringShorthand])

    prop = WithComponentWithShorthands.prop[DateTimeShorthand]("component.dateTime")
    prop.path should equal ("component.dateTime")
    prop.typeKey should equal (typeKey[DateTimeShorthand])
  }

  it should "produce a valid nat key prop for a nested assoc" in {
    import longevity.integration.subdomain.withComponentWithAssoc._
    val prop = WithComponentWithAssoc.prop[Assoc[Associated]]("component.associated")
    prop.path should equal ("component.associated")
    prop.typeKey should equal (typeKey[Assoc[Associated]])
  }

}
