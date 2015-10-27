package longevity.unit.subdomain

import com.github.nscala_time.time.Imports._
import emblem.imports._
import longevity.exceptions._
import org.scalatest._
import longevity.subdomain._

/** unit tests for the proper construction of [[RootEntityType#NatKeyProp nat key props]] */
class NatKeyPropSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "RootEntityType.NatKeyProp.apply(String)"

  it should "throw exception when the specified prop path is empty" in {
    import longevity.integration.allAttributes._
    intercept[EmptyNatKeyPropPathException] {
      AllAttributes.natKeyProp("")
    }
  }

  it should "throw exception when the specified prop path does not map to an actual prop path" in {
    import longevity.integration.allAttributes._
    intercept[NoSuchNatKeyPropPathSegmentException] {
      AllAttributes.natKeyProp("invalidPropPath")
    }

    import longevity.integration.withComponent._
    intercept[NoSuchNatKeyPropPathSegmentException] {
      WithComponent.natKeyProp("component.noSuchPathSegment")
    }
  }

  it should "throw exception when the specified prop path passes through a collection" in {
    import longevity.integration.withComponentList._
    intercept[NonEntityNatKeyPropPathSegmentException] {
      WithComponentList.natKeyProp("components.uri")
    }

    import longevity.integration.withComponentOption._
    intercept[NonEntityNatKeyPropPathSegmentException] {
      WithComponentOption.natKeyProp("component.uri")
    }

    import longevity.integration.withComponentSet._
    intercept[NonEntityNatKeyPropPathSegmentException] {
      WithComponentSet.natKeyProp("components.uri")
    }
  }

  it should "throw exception when the specified prop path terminates with a collection" in {
    import longevity.integration.attributeLists._
    intercept[InvalidNatKeyPropPathLeafException] {
      AttributeLists.natKeyProp("boolean")
    }

    import longevity.integration.attributeOptions._
    intercept[InvalidNatKeyPropPathLeafException] {
      AttributeOptions.natKeyProp("boolean")
    }
 
    import longevity.integration.attributeSets._
    intercept[InvalidNatKeyPropPathLeafException] {
      AttributeSets.natKeyProp("boolean")
    }

    import longevity.integration.withAssocList._
    intercept[InvalidNatKeyPropPathLeafException] {
      WithAssocList.natKeyProp("associated")
    }

    import longevity.integration.withAssocOption._
    intercept[InvalidNatKeyPropPathLeafException] {
      WithAssocOption.natKeyProp("associated")
    }

    import longevity.integration.withAssocSet._
    intercept[InvalidNatKeyPropPathLeafException] {
      WithAssocSet.natKeyProp("associated")
    }

    import longevity.integration.withComponentList._
    intercept[InvalidNatKeyPropPathLeafException] {
      WithComponentList.natKeyProp("components")
    }

    import longevity.integration.withComponentOption._
    intercept[InvalidNatKeyPropPathLeafException] {
      WithComponentOption.natKeyProp("component")
    }

    import longevity.integration.withComponentSet._
    intercept[InvalidNatKeyPropPathLeafException] {
      WithComponentSet.natKeyProp("components")
    }

    import longevity.integration.withComponent._
    intercept[InvalidNatKeyPropPathLeafException] {
      WithComponent.natKeyProp("component.tags")
    }
  }

  it should "produce a valid nat key prop for basic types" in {
    import longevity.integration.allAttributes._

    var prop: NatKeyProp[AllAttributes] = null

    prop = AllAttributes.natKeyProp("boolean")
    prop.path should equal ("boolean")
    prop.typeKey should equal (typeKey[Boolean])

    prop = AllAttributes.natKeyProp("char")
    prop.path should equal ("char")
    prop.typeKey should equal (typeKey[Char])

    prop = AllAttributes.natKeyProp("double")
    prop.path should equal ("double")
    prop.typeKey should equal (typeKey[Double])

    prop = AllAttributes.natKeyProp("float")
    prop.path should equal ("float")
    prop.typeKey should equal (typeKey[Float])

    prop = AllAttributes.natKeyProp("int")
    prop.path should equal ("int")
    prop.typeKey should equal (typeKey[Int])

    prop = AllAttributes.natKeyProp("long")
    prop.path should equal ("long")
    prop.typeKey should equal (typeKey[Long])

    prop = AllAttributes.natKeyProp("string")
    prop.path should equal ("string")
    prop.typeKey should equal (typeKey[String])

    prop = AllAttributes.natKeyProp("dateTime")
    prop.path should equal ("dateTime")
    prop.typeKey should equal (typeKey[DateTime])
  }

  it should "produce a valid nat key prop for shorthand types" in {
    import longevity.integration.allShorthands._

    var prop: NatKeyProp[AllShorthands] = null

    prop = AllShorthands.natKeyProp("boolean")
    prop.path should equal ("boolean")
    prop.typeKey should equal (typeKey[BooleanShorthand])

    prop = AllShorthands.natKeyProp("char")
    prop.path should equal ("char")
    prop.typeKey should equal (typeKey[CharShorthand])

    prop = AllShorthands.natKeyProp("double")
    prop.path should equal ("double")
    prop.typeKey should equal (typeKey[DoubleShorthand])

    prop = AllShorthands.natKeyProp("float")
    prop.path should equal ("float")
    prop.typeKey should equal (typeKey[FloatShorthand])

    prop = AllShorthands.natKeyProp("int")
    prop.path should equal ("int")
    prop.typeKey should equal (typeKey[IntShorthand])

    prop = AllShorthands.natKeyProp("long")
    prop.path should equal ("long")
    prop.typeKey should equal (typeKey[LongShorthand])

    prop = AllShorthands.natKeyProp("string")
    prop.path should equal ("string")
    prop.typeKey should equal (typeKey[StringShorthand])

    prop = AllShorthands.natKeyProp("dateTime")
    prop.path should equal ("dateTime")
    prop.typeKey should equal (typeKey[DateTimeShorthand])
  }

  it should "produce a valid nat key prop for an assoc" in {
    import longevity.integration.withAssoc._
    val prop = WithAssoc.natKeyProp("associated")
    prop.path should equal ("associated")
    prop.typeKey should equal (typeKey[Assoc[Associated]])
  }

  it should "produce a valid nat key prop for a nested basic type" in {
    import longevity.integration.withComponent._
    val prop = WithComponent.natKeyProp("component.uri")
    prop.path should equal ("component.uri")
    prop.typeKey should equal (typeKey[String])
  }

  it should "produce a valid nat key prop for shorthand types in nested components" in {
    import longevity.integration.withComponentWithShorthands._
    var prop: NatKeyProp[WithComponentWithShorthands] = null

    prop = WithComponentWithShorthands.natKeyProp("component.boolean")
    prop.path should equal ("component.boolean")
    prop.typeKey should equal (typeKey[BooleanShorthand])

    prop = WithComponentWithShorthands.natKeyProp("component.char")
    prop.path should equal ("component.char")
    prop.typeKey should equal (typeKey[CharShorthand])

    prop = WithComponentWithShorthands.natKeyProp("component.double")
    prop.path should equal ("component.double")
    prop.typeKey should equal (typeKey[DoubleShorthand])

    prop = WithComponentWithShorthands.natKeyProp("component.float")
    prop.path should equal ("component.float")
    prop.typeKey should equal (typeKey[FloatShorthand])

    prop = WithComponentWithShorthands.natKeyProp("component.int")
    prop.path should equal ("component.int")
    prop.typeKey should equal (typeKey[IntShorthand])

    prop = WithComponentWithShorthands.natKeyProp("component.long")
    prop.path should equal ("component.long")
    prop.typeKey should equal (typeKey[LongShorthand])

    prop = WithComponentWithShorthands.natKeyProp("component.string")
    prop.path should equal ("component.string")
    prop.typeKey should equal (typeKey[StringShorthand])

    prop = WithComponentWithShorthands.natKeyProp("component.dateTime")
    prop.path should equal ("component.dateTime")
    prop.typeKey should equal (typeKey[DateTimeShorthand])
  }

  it should "produce a valid nat key prop for a nested assoc" in {
    import longevity.integration.withComponentWithAssoc._
    val prop = WithComponentWithAssoc.natKeyProp("component.associated")
    prop.path should equal ("component.associated")
    prop.typeKey should equal (typeKey[Assoc[Associated]])
  }

}
