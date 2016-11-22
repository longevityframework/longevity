package longevity.unit.subdomain.ptype

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[Key keys]] */
class KeySpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "Key.toString"

  it should "produce a readable string containing type names for the persistent and the keyval" in {
    import longevity.integration.subdomain.basics._
    Basics.keySet.head.toString should equal ("Key[Basics,BasicsId]")
  }

  behavior of "Key.keyValProp"

  it should "produce the right property" in {
    import longevity.integration.subdomain.basics._
    Basics.keySet.head.keyValProp should equal (Basics.props.id)
  }

}
