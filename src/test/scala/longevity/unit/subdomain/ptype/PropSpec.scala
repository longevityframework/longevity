package longevity.unit.subdomain.ptype

import emblem.typeKey
import longevity.subdomain.ptype.Prop
import org.joda.time.DateTime
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper construction of [[Prop properties]] */
class PropSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "Prop.apply(String)"

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

  it should "produce a valid prop for multi prop embeddables" in {
    import longevity.integration.subdomain.component._
    val prop = WithComponent.prop[Component]("component")
    prop.path should equal ("component")
    prop.propTypeKey should equal (typeKey[Component])
  }

  it should "produce a valid prop for a key vals" in {
    import longevity.integration.subdomain.foreignKey._
    val prop = WithForeignKey.prop[AssociatedId]("associated")
    prop.path should equal ("associated")
    prop.propTypeKey should equal (typeKey[AssociatedId])
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
