package longevity.subdomain

import emblem.imports._
import org.scalatest._

/** unit tests for the proper construction of [[RootEntityType#NatKeyProp nat key props]] */
@longevity.UnitTest
class NatKeyPropSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "RootEntityType.NatKeyProp.apply(String)"

  it should "produce a valid nat key prop for basic types" in {
    val uriProp = WithSimpleNatKey.NatKeyProp("uri")
    uriProp.path should equal ("uri")
    uriProp.typeKey should equal (typeKey[String])

    val topScoreProp = WithSimpleNatKey.NatKeyProp("topScore")
    topScoreProp.path should equal ("topScore")
    topScoreProp.typeKey should equal (typeKey[Int])
  }

}
