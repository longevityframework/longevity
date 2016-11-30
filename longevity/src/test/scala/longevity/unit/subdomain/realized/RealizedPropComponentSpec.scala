package longevity.unit.subdomain.realized

import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of a [[RealizedPropComponent]] */
class RealizedPropComponentSpec extends FlatSpec with GivenWhenThen with Matchers {

  import longevity.unit.blogCore._

  val userRealizedPType = subdomain.realizedPTypes(User)
  val realizedProp = userRealizedPType.realizedProps(User.props.email)

  realizedProp.realizedPropComponents.size should equal (1)

  val component = realizedProp.realizedPropComponents.head

  behavior of "RealizedPropComponent.componentTypeKey"
  it should "produce the appropriate type key for the basic type" in {
    component.componentTypeKey should equal (typeKey[String])
  }

  behavior of "RealizedPropComponent.ordering"
  it should "produce and appropriate basic type" in {
    val ordering = component.ordering.asInstanceOf[Ordering[String]]
    ordering.compare("x", "x") should equal (0)
    ordering.compare("x", "y") should equal (-1)
    ordering.compare("y", "x") should equal (1)
  }

  behavior of "RealizedPropComponent.get"
  it should "produce the appropriate basic value from a property value" in {
    val email = Email("email22")
    component.get(email) should equal (email.email)
  }

}
