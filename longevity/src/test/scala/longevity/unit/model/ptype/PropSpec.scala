package longevity.unit.model.ptype

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper construction and behavior of a [[Prop property]] */
class PropSpec extends FlatSpec with GivenWhenThen with Matchers {

  import longevity.unit.blogCore._

  behavior of "Prop.toString"

  it should "produce a string indicating its path" in {
    User.props.username.toString should equal ("username")
  }

}
