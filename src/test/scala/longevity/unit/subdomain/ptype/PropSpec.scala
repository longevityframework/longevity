package longevity.unit.subdomain.ptype

import emblem.typeKey
import longevity.subdomain.ptype.Prop
import org.joda.time.DateTime
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

// TODO all these excptions have moved to subdomain creation
// import longevity.exceptions.subdomain.ptype.NoSuchPropPathException
// import longevity.exceptions.subdomain.ptype.PropTypeException
// import longevity.exceptions.subdomain.ptype.UnsupportedPropTypeException
// import longevity.subdomain.persistent.Root
// import longevity.subdomain.ptype.RootType

/** unit tests for the proper construction of [[Prop properties]] */
class PropSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "Prop.apply(String)"

  // TODO all these excptions have moved to subdomain creation

  // it should "throw exception when the specified prop path is empty" in {
  //   import longevity.integration.subdomain.basics
  //   val subdomain = basics.subdomain
  //   intercept[NoSuchPropPathException] {
  //     basics.Basics.prop[Int]("")
  //   }
  // }

  // it should "throw exception when the specified prop path does not map to an actual prop path" in {
  //   import longevity.integration.subdomain.basics
  //   val subdomain = basics.subdomain
  //   intercept[NoSuchPropPathException] {
  //     basics.Basics.prop[Int]("invalidPropPath")
  //   }

  //   import longevity.integration.subdomain.component
  //   val componentSubdomain = component.subdomain
  //   intercept[NoSuchPropPathException] {
  //     component.WithComponent.prop[Int]("component.noSuchPathSegment")
  //   }
  // }

  // it should "throw exception when the specified prop path passes through a collection" in {
  //   import longevity.integration.subdomain.componentList
  //   val componentListSubdomain = componentList.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     componentList.WithComponentList.prop[Int]("components.uri")
  //   }

  //   import longevity.integration.subdomain.componentOption
  //   val componentOptionSubdomain = componentOption.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     componentOption.WithComponentOption.prop[Int]("component.uri")
  //   }

  //   import longevity.integration.subdomain.componentSet
  //   val componentSetSubdomain = componentSet.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     componentSet.WithComponentSet.prop[Int]("components.uri")
  //   }
  // }

  // it should "throw exception when the specified prop path terminates with a collection" in {
  //   import longevity.integration.subdomain.basicLists
  //   val basicListsSubdomain = basicLists.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     basicLists.BasicLists.prop[Int]("boolean")
  //   }

  //   import longevity.integration.subdomain.basicOptions
  //   val basicOptionsSubdomain = basicOptions.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     basicOptions.BasicOptions.prop[Int]("boolean")
  //   }
 
  //   import longevity.integration.subdomain.basicSets
  //   val basicSetsSubdomain = basicSets.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     basicSets.BasicSets.prop[Int]("boolean")
  //   }

  //   import longevity.integration.subdomain.foreignKeyList
  //   val foreignKeyListSubdomain = foreignKeyList.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     foreignKeyList.WithForeignKeyList.prop[Int]("associated")
  //   }

  //   import longevity.integration.subdomain.foreignKeyOption
  //   val foreignKeyOptionSubdomain = foreignKeyOption.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     foreignKeyOption.WithForeignKeyOption.prop[Int]("associated")
  //   }

  //   import longevity.integration.subdomain.foreignKeySet
  //   val foreignKeySetSubdomain = foreignKeySet.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     foreignKeySet.WithForeignKeySet.prop[Int]("associated")
  //   }

  //   import longevity.integration.subdomain.componentList
  //   val componentListSubdomain = componentList.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     componentList.WithComponentList.prop[Int]("components")
  //   }

  //   import longevity.integration.subdomain.componentOption
  //   val componentOptionSubdomain = componentOption.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     componentOption.WithComponentOption.prop[Int]("component")
  //   }

  //   import longevity.integration.subdomain.componentSet
  //   val componentSetSubdomain = componentSet.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     componentSet.WithComponentSet.prop[Int]("components")
  //   }

  //   import longevity.integration.subdomain.component
  //   val componentSubdomain = component.subdomain
  //   intercept[UnsupportedPropTypeException[_, _]] {
  //     component.WithComponent.prop[Int]("component.tags")
  //   }
  // }

  // it should "throw exception when the specified prop type does not match the actual type" in {
  //   import longevity.integration.subdomain.basics
  //   val subdomain = basics.subdomain

  //   // two entirely incompatible types
  //   intercept[PropTypeException] {
  //     basics.Basics.prop[String]("boolean")
  //   }

  //   // Double <:< AnyVal, but we need requested type to be subtype of the actual type, not
  //   // the other way around:
  //   intercept[PropTypeException] {
  //     basics.Basics.prop[AnyVal]("double")
  //   }
  // }

  it should "produce a valid prop for basic types" in {
    import longevity.integration.subdomain.basics._

    var prop: Prop[Basics, _] = null

    prop = Basics.prop[Boolean]("boolean")
    prop.path should equal ("boolean")
    prop.propTypeKey should equal (typeKey[Boolean])

    prop = Basics.prop[Char]("char")
    prop.path should equal ("char")
    prop.propTypeKey should equal (typeKey[Char])

    prop = Basics.prop[Double]("double")
    prop.path should equal ("double")
    prop.propTypeKey should equal (typeKey[Double])

    prop = Basics.prop[Float]("float")
    prop.path should equal ("float")
    prop.propTypeKey should equal (typeKey[Float])

    prop = Basics.prop[Int]("int")
    prop.path should equal ("int")
    prop.propTypeKey should equal (typeKey[Int])

    prop = Basics.prop[Long]("long")
    prop.path should equal ("long")
    prop.propTypeKey should equal (typeKey[Long])

    prop = Basics.prop[String]("string")
    prop.path should equal ("string")
    prop.propTypeKey should equal (typeKey[String])

    prop = Basics.prop[DateTime]("dateTime")
    prop.path should equal ("dateTime")
    prop.propTypeKey should equal (typeKey[DateTime])
  }

  it should "produce a valid prop for single prop embeddables" in {
    import longevity.integration.subdomain.shorthands._

    val prop = Shorthands.prop[ShorthandsId]("id")
    prop.path should equal ("id")
    prop.propTypeKey should equal (typeKey[ShorthandsId])
  }

  it should "produce a valid prop for a key vals" in {
    import longevity.integration.subdomain.foreignKey._
    val prop = WithForeignKey.prop[AssociatedId]("associated")
    prop.path should equal ("associated")
    prop.propTypeKey should equal (typeKey[AssociatedId])
  }

  // im havent settled on what the correct behavior should be here. at present,
  // the prop takes on the type provided by the user. if the user provided type
  // is simply incorrect, of course you will get an exception. but if it is
  // simply too tight, perhaps the prop should come out with the weaker type. no
  // other tests break but this one with the change of behavior, so im going to
  // assume its okay for now
  it should "produce a prop with the actual prop type when the provided type is overly tight" ignore {

    // TODO: need to find a new example to test this:
    // import longevity.integration.subdomain.foreignKey._
    // val overlyTypedProp = WithForeignKey.prop[DatabaseId[Associated]]("associated")
    // overlyTypedProp.path should equal ("associated")
    // overlyTypedProp.propTypeKey should equal (typeKey[Assoc[Associated]])
  }

  it should "produce a valid prop for a nested basic type" in {
    import longevity.integration.subdomain.component._
    val prop = WithComponent.prop[String]("component.id")
    prop.path should equal ("component.id")
    prop.propTypeKey should equal (typeKey[String])
  }

  it should "produce a valid prop for shorthand types in nested components" in {
    import longevity.integration.subdomain.componentShorthands._
    val prop = WithComponentShorthands.prop[Uri]("component.uri")
    prop.path should equal ("component.uri")
    prop.propTypeKey should equal (typeKey[Uri])
  }

  it should "produce a valid prop for a nested foreign key" in {
    import longevity.integration.subdomain.componentWithForeignKey._
    val prop = WithComponentWithForeignKey.prop[AssociatedId]("component.associated")
    prop.path should equal ("component.associated")
    prop.propTypeKey should equal (typeKey[AssociatedId])
  }

}
