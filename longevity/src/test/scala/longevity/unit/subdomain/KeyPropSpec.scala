package longevity.unit.subdomain

import com.github.nscala_time.time.Imports._
import emblem.imports._
import longevity.exceptions.subdomain._
import org.scalatest._
import longevity.subdomain._

/** unit tests for the proper construction of [[RootEntityType#KeyProp nat key props]] */
class KeyPropSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "RootEntityType.KeyProp.apply(String)"

  it should "throw exception when the specified prop path is empty" in {
    import longevity.integration.subdomain.allAttributes._
    intercept[EmptyKeyPropPathException] {
      AllAttributes.keyProp("")
    }
  }

  it should "throw exception when the specified prop path does not map to an actual prop path" in {
    import longevity.integration.subdomain.allAttributes._
    intercept[NoSuchKeyPropPathSegmentException] {
      AllAttributes.keyProp("invalidPropPath")
    }

    import longevity.integration.subdomain.withComponent._
    intercept[NoSuchKeyPropPathSegmentException] {
      WithComponent.keyProp("component.noSuchPathSegment")
    }
  }

  it should "throw exception when the specified prop path passes through a collection" in {
    import longevity.integration.subdomain.withComponentList._
    intercept[NonEntityKeyPropPathSegmentException] {
      WithComponentList.keyProp("components.uri")
    }

    import longevity.integration.subdomain.withComponentOption._
    intercept[NonEntityKeyPropPathSegmentException] {
      WithComponentOption.keyProp("component.uri")
    }

    import longevity.integration.subdomain.withComponentSet._
    intercept[NonEntityKeyPropPathSegmentException] {
      WithComponentSet.keyProp("components.uri")
    }
  }

  it should "throw exception when the specified prop path terminates with a collection" in {
    import longevity.integration.subdomain.attributeLists._
    intercept[InvalidKeyPropPathLeafException] {
      AttributeLists.keyProp("boolean")
    }

    import longevity.integration.subdomain.attributeOptions._
    intercept[InvalidKeyPropPathLeafException] {
      AttributeOptions.keyProp("boolean")
    }
 
    import longevity.integration.subdomain.attributeSets._
    intercept[InvalidKeyPropPathLeafException] {
      AttributeSets.keyProp("boolean")
    }

    import longevity.integration.subdomain.withAssocList._
    intercept[InvalidKeyPropPathLeafException] {
      WithAssocList.keyProp("associated")
    }

    import longevity.integration.subdomain.withAssocOption._
    intercept[InvalidKeyPropPathLeafException] {
      WithAssocOption.keyProp("associated")
    }

    import longevity.integration.subdomain.withAssocSet._
    intercept[InvalidKeyPropPathLeafException] {
      WithAssocSet.keyProp("associated")
    }

    import longevity.integration.subdomain.withComponentList._
    intercept[InvalidKeyPropPathLeafException] {
      WithComponentList.keyProp("components")
    }

    import longevity.integration.subdomain.withComponentOption._
    intercept[InvalidKeyPropPathLeafException] {
      WithComponentOption.keyProp("component")
    }

    import longevity.integration.subdomain.withComponentSet._
    intercept[InvalidKeyPropPathLeafException] {
      WithComponentSet.keyProp("components")
    }

    import longevity.integration.subdomain.withComponent._
    intercept[InvalidKeyPropPathLeafException] {
      WithComponent.keyProp("component.tags")
    }
  }

  it should "produce a valid nat key prop for basic types" in {
    import longevity.integration.subdomain.allAttributes._

    var prop: KeyProp[AllAttributes] = null

    prop = AllAttributes.keyProp("boolean")
    prop.path should equal ("boolean")
    prop.typeKey should equal (typeKey[Boolean])

    prop = AllAttributes.keyProp("char")
    prop.path should equal ("char")
    prop.typeKey should equal (typeKey[Char])

    prop = AllAttributes.keyProp("double")
    prop.path should equal ("double")
    prop.typeKey should equal (typeKey[Double])

    prop = AllAttributes.keyProp("float")
    prop.path should equal ("float")
    prop.typeKey should equal (typeKey[Float])

    prop = AllAttributes.keyProp("int")
    prop.path should equal ("int")
    prop.typeKey should equal (typeKey[Int])

    prop = AllAttributes.keyProp("long")
    prop.path should equal ("long")
    prop.typeKey should equal (typeKey[Long])

    prop = AllAttributes.keyProp("string")
    prop.path should equal ("string")
    prop.typeKey should equal (typeKey[String])

    prop = AllAttributes.keyProp("dateTime")
    prop.path should equal ("dateTime")
    prop.typeKey should equal (typeKey[DateTime])
  }

  it should "produce a valid nat key prop for shorthand types" in {
    import longevity.integration.subdomain.allShorthands._

    var prop: KeyProp[AllShorthands] = null

    prop = AllShorthands.keyProp("boolean")
    prop.path should equal ("boolean")
    prop.typeKey should equal (typeKey[BooleanShorthand])

    prop = AllShorthands.keyProp("char")
    prop.path should equal ("char")
    prop.typeKey should equal (typeKey[CharShorthand])

    prop = AllShorthands.keyProp("double")
    prop.path should equal ("double")
    prop.typeKey should equal (typeKey[DoubleShorthand])

    prop = AllShorthands.keyProp("float")
    prop.path should equal ("float")
    prop.typeKey should equal (typeKey[FloatShorthand])

    prop = AllShorthands.keyProp("int")
    prop.path should equal ("int")
    prop.typeKey should equal (typeKey[IntShorthand])

    prop = AllShorthands.keyProp("long")
    prop.path should equal ("long")
    prop.typeKey should equal (typeKey[LongShorthand])

    prop = AllShorthands.keyProp("string")
    prop.path should equal ("string")
    prop.typeKey should equal (typeKey[StringShorthand])

    prop = AllShorthands.keyProp("dateTime")
    prop.path should equal ("dateTime")
    prop.typeKey should equal (typeKey[DateTimeShorthand])
  }

  it should "produce a valid nat key prop for an assoc" in {
    import longevity.integration.subdomain.withAssoc._
    val prop = WithAssoc.keyProp("associated")
    prop.path should equal ("associated")
    prop.typeKey should equal (typeKey[Assoc[Associated]])
  }

  it should "produce a valid nat key prop for a nested basic type" in {
    import longevity.integration.subdomain.withComponent._
    val prop = WithComponent.keyProp("component.uri")
    prop.path should equal ("component.uri")
    prop.typeKey should equal (typeKey[String])
  }

  it should "produce a valid nat key prop for shorthand types in nested components" in {
    import longevity.integration.subdomain.withComponentWithShorthands._
    var prop: KeyProp[WithComponentWithShorthands] = null

    prop = WithComponentWithShorthands.keyProp("component.boolean")
    prop.path should equal ("component.boolean")
    prop.typeKey should equal (typeKey[BooleanShorthand])

    prop = WithComponentWithShorthands.keyProp("component.char")
    prop.path should equal ("component.char")
    prop.typeKey should equal (typeKey[CharShorthand])

    prop = WithComponentWithShorthands.keyProp("component.double")
    prop.path should equal ("component.double")
    prop.typeKey should equal (typeKey[DoubleShorthand])

    prop = WithComponentWithShorthands.keyProp("component.float")
    prop.path should equal ("component.float")
    prop.typeKey should equal (typeKey[FloatShorthand])

    prop = WithComponentWithShorthands.keyProp("component.int")
    prop.path should equal ("component.int")
    prop.typeKey should equal (typeKey[IntShorthand])

    prop = WithComponentWithShorthands.keyProp("component.long")
    prop.path should equal ("component.long")
    prop.typeKey should equal (typeKey[LongShorthand])

    prop = WithComponentWithShorthands.keyProp("component.string")
    prop.path should equal ("component.string")
    prop.typeKey should equal (typeKey[StringShorthand])

    prop = WithComponentWithShorthands.keyProp("component.dateTime")
    prop.path should equal ("component.dateTime")
    prop.typeKey should equal (typeKey[DateTimeShorthand])
  }

  it should "produce a valid nat key prop for a nested assoc" in {
    import longevity.integration.subdomain.withComponentWithAssoc._
    val prop = WithComponentWithAssoc.keyProp("component.associated")
    prop.path should equal ("component.associated")
    prop.typeKey should equal (typeKey[Assoc[Associated]])
  }

}
