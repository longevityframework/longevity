package longevity.unit.model.realized

import typekey.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper construction and behavior of a [[RealizedProp realized property]] */
class RealizedPropSpec extends FlatSpec with GivenWhenThen with Matchers {

  import longevity.unit.blogCore._

  val userRealizedPType = BlogCore.modelType.realizedPTypes(User)
  val realizedProp = userRealizedPType.realizedProps(User.props.email)

  val email = Email("email28")
  val user = User("username", email, "fullname")

  behavior of "RealizedProp.propTypeKey"
  it should "produce the appropriate type key for the property type" in {
    realizedProp.propTypeKey should equal (typeKey[Email])
  }

  behavior of "RealizedProp.inlinedPath"
  it should "produce the right path" in {
    realizedProp.inlinedPath should equal ("email")
  }

  behavior of "RealizedProp.propVal"
  it should "produce the property value for the supplied persistent" in {
    realizedProp.propVal(user) should equal (email)
  }

  behavior of "RealizedProp.updatePropVal"
  it should "produce a copy of the persistent with the updated property value" in {
    val email2 = Email("email878787")
    realizedProp.updatePropVal(user, email2) should equal (user.copy(email = email2))
  }

  behavior of "RealizedProp.ordering"

  it should "compare prop values correctly for simple properties" in {
    val ordering = realizedProp.ordering
    ordering.compare(Email("x"), Email("x")) should equal (0)
    ordering.compare(Email("x"), Email("y")) should equal (-1)
    ordering.compare(Email("y"), Email("x")) should equal (1)
  }

  it should "compare prop values correctly for compound properties" in {
    import longevity.integration.model.component._

    val realizedPType = DomainModel.modelType.realizedPTypes(WithComponent)
    val realizedProp = realizedPType.realizedProps(WithComponent.props.component)

    val component1 = Component("id1", "tag")
    val component2 = Component("id2", "tag")

    val ordering = realizedProp.ordering
    ordering.compare(component1, component1) should equal (0)
    ordering.compare(component1, component2) should equal (-1)
    ordering.compare(component2, component1) should equal (1)
  }

  behavior of "RealizedProp.toString"
  it should "produce a string indicating its path" in {
    realizedProp.toString should equal ("email")
  }

}
