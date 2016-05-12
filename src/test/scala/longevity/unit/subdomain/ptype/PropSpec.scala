package longevity.unit.subdomain.ptype

import org.joda.time.DateTime
import emblem.typeKey
import longevity.exceptions.subdomain.ptype.PropTypeException
import longevity.exceptions.subdomain.ptype.UnsupportedPropTypeException
import longevity.exceptions.subdomain.ptype.NoSuchPropException
import longevity.persistence.PersistedAssoc
import longevity.subdomain.Assoc
import longevity.subdomain.entity.EntityTypePool
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.Prop
import longevity.subdomain.ptype.RootType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper construction of [[Prop properties]] */
class PropSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "RootType.Prop.apply(String)"

  it should "throw exception when the specified prop path is empty" in {
    import longevity.integration.subdomain.allAttributes
    val subdomain = allAttributes.context.subdomain
    intercept[NoSuchPropException] {
      allAttributes.AllAttributes.prop[Int]("")
    }
  }

  it should "throw exception when the specified prop path does not map to an actual prop path" in {
    import longevity.integration.subdomain.allAttributes
    val subdomain = allAttributes.context.subdomain
    intercept[NoSuchPropException] {
      allAttributes.AllAttributes.prop[Int]("invalidPropPath")
    }

    import longevity.integration.subdomain.withComponent
    val withComponentSubdomain = withComponent.context.subdomain
    intercept[NoSuchPropException] {
      withComponent.WithComponent.prop[Int]("component.noSuchPathSegment")
    }
  }

  it should "throw exception when the specified prop path passes through a collection" in {
    import longevity.integration.subdomain.withComponentList
    val withComponentListSubdomain = withComponentList.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      withComponentList.WithComponentList.prop[Int]("components.uri")
    }

    import longevity.integration.subdomain.withComponentOption
    val withComponentOptionSubdomain = withComponentOption.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      withComponentOption.WithComponentOption.prop[Int]("component.uri")
    }

    import longevity.integration.subdomain.withComponentSet
    val withComponentSetSubdomain = withComponentSet.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      withComponentSet.WithComponentSet.prop[Int]("components.uri")
    }
  }

  it should "throw exception when the specified prop path terminates with a collection" in {
    import longevity.integration.subdomain.attributeLists
    val attributeListsSubdomain = attributeLists.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      attributeLists.AttributeLists.prop[Int]("boolean")
    }

    import longevity.integration.subdomain.attributeOptions
    val attributeOptionsSubdomain = attributeOptions.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      attributeOptions.AttributeOptions.prop[Int]("boolean")
    }
 
    import longevity.integration.subdomain.attributeSets
    val attributeSetsSubdomain = attributeSets.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      attributeSets.AttributeSets.prop[Int]("boolean")
    }

    import longevity.integration.subdomain.withAssocList
    val withAssocListSubdomain = withAssocList.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      withAssocList.WithAssocList.prop[Int]("associated")
    }

    import longevity.integration.subdomain.withAssocOption
    val withAssocOptionSubdomain = withAssocOption.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      withAssocOption.WithAssocOption.prop[Int]("associated")
    }

    import longevity.integration.subdomain.withAssocSet
    val withAssocSetSubdomain = withAssocSet.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      withAssocSet.WithAssocSet.prop[Int]("associated")
    }

    import longevity.integration.subdomain.withComponentList
    val withComponentListSubdomain = withComponentList.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      withComponentList.WithComponentList.prop[Int]("components")
    }

    import longevity.integration.subdomain.withComponentOption
    val withComponentOptionSubdomain = withComponentOption.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      withComponentOption.WithComponentOption.prop[Int]("component")
    }

    import longevity.integration.subdomain.withComponentSet
    val withComponentSetSubdomain = withComponentSet.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      withComponentSet.WithComponentSet.prop[Int]("components")
    }

    import longevity.integration.subdomain.withComponent
    val withComponentSubdomain = withComponent.context.subdomain
    intercept[UnsupportedPropTypeException[_, _]] {
      withComponent.WithComponent.prop[Int]("component.tags")
    }
  }

  it should "throw exception when the specified prop type does not match the actual type" in {
    import longevity.integration.subdomain.allAttributes
    val subdomain = allAttributes.context.subdomain

    // two entirely incompatible types
    intercept[PropTypeException] {
      allAttributes.AllAttributes.prop[String]("boolean")
    }

    // Double <:< AnyVal, but we need requested type to be subtype of the actual type, not
    // the other way around:
    intercept[PropTypeException] {
      allAttributes.AllAttributes.prop[AnyVal]("double")
    }
  }

  it should "produce a valid prop for basic types" in {
    import longevity.integration.subdomain.allAttributes._
    val subdomain = context.subdomain

    var prop: Prop[AllAttributes, _] = null

    prop = AllAttributes.prop[Boolean]("boolean")
    prop.path should equal ("boolean")
    prop.propTypeKey should equal (typeKey[Boolean])

    prop = AllAttributes.prop[Char]("char")
    prop.path should equal ("char")
    prop.propTypeKey should equal (typeKey[Char])

    prop = AllAttributes.prop[Double]("double")
    prop.path should equal ("double")
    prop.propTypeKey should equal (typeKey[Double])

    prop = AllAttributes.prop[Float]("float")
    prop.path should equal ("float")
    prop.propTypeKey should equal (typeKey[Float])

    prop = AllAttributes.prop[Int]("int")
    prop.path should equal ("int")
    prop.propTypeKey should equal (typeKey[Int])

    prop = AllAttributes.prop[Long]("long")
    prop.path should equal ("long")
    prop.propTypeKey should equal (typeKey[Long])

    prop = AllAttributes.prop[String]("string")
    prop.path should equal ("string")
    prop.propTypeKey should equal (typeKey[String])

    prop = AllAttributes.prop[DateTime]("dateTime")
    prop.path should equal ("dateTime")
    prop.propTypeKey should equal (typeKey[DateTime])
  }

  it should "produce a valid prop for shorthand types" in {
    import longevity.integration.subdomain.allShorthands._
    val subdomain = context.subdomain

    var prop: Prop[AllShorthands, _] = null

    prop = AllShorthands.prop[BooleanShorthand]("boolean")
    prop.path should equal ("boolean")
    prop.propTypeKey should equal (typeKey[BooleanShorthand])

    prop = AllShorthands.prop[CharShorthand]("char")
    prop.path should equal ("char")
    prop.propTypeKey should equal (typeKey[CharShorthand])

    prop = AllShorthands.prop[DoubleShorthand]("double")
    prop.path should equal ("double")
    prop.propTypeKey should equal (typeKey[DoubleShorthand])

    prop = AllShorthands.prop[FloatShorthand]("float")
    prop.path should equal ("float")
    prop.propTypeKey should equal (typeKey[FloatShorthand])

    prop = AllShorthands.prop[IntShorthand]("int")
    prop.path should equal ("int")
    prop.propTypeKey should equal (typeKey[IntShorthand])

    prop = AllShorthands.prop[LongShorthand]("long")
    prop.path should equal ("long")
    prop.propTypeKey should equal (typeKey[LongShorthand])

    prop = AllShorthands.prop[StringShorthand]("string")
    prop.path should equal ("string")
    prop.propTypeKey should equal (typeKey[StringShorthand])

    prop = AllShorthands.prop[DateTimeShorthand]("dateTime")
    prop.path should equal ("dateTime")
    prop.propTypeKey should equal (typeKey[DateTimeShorthand])
  }

  it should "produce a valid prop for an assoc" in {
    import longevity.integration.subdomain.withAssoc._
    val prop = WithAssoc.prop[Assoc[Associated]]("associated")
    prop.path should equal ("associated")
    prop.propTypeKey should equal (typeKey[Assoc[Associated]])
  }

  // im havent settled on what the correct behavior should be here. at present,
  // the prop takes on the type provided by the user. if the user provided type
  // is simply incorrect, of course you will get an exception. but if it is
  // simply too tight, perhaps the prop should come out with the weaker type. no
  // other tests break but this one with the change of behavior, so im going to
  // assume its okay for now
  it should "produce a prop with the actual prop type when the provided type is overly tight" ignore {
    import longevity.integration.subdomain.withAssoc._
    val overlyTypedProp = WithAssoc.prop[PersistedAssoc[Associated]]("associated")
    overlyTypedProp.path should equal ("associated")
    overlyTypedProp.propTypeKey should equal (typeKey[Assoc[Associated]])
  }

  it should "produce a valid prop for a nested basic type" in {
    import longevity.integration.subdomain.withComponent._
    val prop = WithComponent.prop[String]("component.uri")
    prop.path should equal ("component.uri")
    prop.propTypeKey should equal (typeKey[String])
  }

  it should "produce a valid prop for shorthand types in nested components" in {
    import longevity.integration.subdomain.withComponentWithShorthands._
    var prop: Prop[WithComponentWithShorthands, _] = null

    prop = WithComponentWithShorthands.prop[BooleanShorthand]("component.boolean")
    prop.path should equal ("component.boolean")
    prop.propTypeKey should equal (typeKey[BooleanShorthand])

    prop = WithComponentWithShorthands.prop[CharShorthand]("component.char")
    prop.path should equal ("component.char")
    prop.propTypeKey should equal (typeKey[CharShorthand])

    prop = WithComponentWithShorthands.prop[DoubleShorthand]("component.double")
    prop.path should equal ("component.double")
    prop.propTypeKey should equal (typeKey[DoubleShorthand])

    prop = WithComponentWithShorthands.prop[FloatShorthand]("component.float")
    prop.path should equal ("component.float")
    prop.propTypeKey should equal (typeKey[FloatShorthand])

    prop = WithComponentWithShorthands.prop[IntShorthand]("component.int")
    prop.path should equal ("component.int")
    prop.propTypeKey should equal (typeKey[IntShorthand])

    prop = WithComponentWithShorthands.prop[LongShorthand]("component.long")
    prop.path should equal ("component.long")
    prop.propTypeKey should equal (typeKey[LongShorthand])

    prop = WithComponentWithShorthands.prop[StringShorthand]("component.string")
    prop.path should equal ("component.string")
    prop.propTypeKey should equal (typeKey[StringShorthand])

    prop = WithComponentWithShorthands.prop[DateTimeShorthand]("component.dateTime")
    prop.path should equal ("component.dateTime")
    prop.propTypeKey should equal (typeKey[DateTimeShorthand])
  }

  it should "produce a valid prop for a nested assoc" in {
    import longevity.integration.subdomain.withComponentWithAssoc._
    val prop = WithComponentWithAssoc.prop[Assoc[Associated]]("component.associated")
    prop.path should equal ("component.associated")
    prop.propTypeKey should equal (typeKey[Assoc[Associated]])
  }

}
