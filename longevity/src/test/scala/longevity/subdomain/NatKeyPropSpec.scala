package longevity.subdomain

import emblem.imports._
import longevity.exceptions.InvalidNatKeyPropPathException
import org.scalatest._

/** unit tests for the proper construction of [[RootEntityType#NatKeyProp nat key props]] */
@longevity.UnitTest
class NatKeyPropSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "RootEntityType.NatKeyProp.apply(String)"

  it should "throw InvalidNatKeyPropPathException when the specified prop path does not map " +
  "to an actual prop path" in {
    intercept[InvalidNatKeyPropPathException] {
      WithSimpleNatKey.NatKeyProp("invalidPropPath")
    }
  }

  it should "produce a valid nat key prop for basic types" in {
    val uriProp = WithSimpleNatKey.NatKeyProp("uri")
    uriProp.path should equal ("uri")
    uriProp.typeKey should equal (typeKey[String])

    val topScoreProp = WithSimpleNatKey.NatKeyProp("topScore")
    topScoreProp.path should equal ("topScore")
    topScoreProp.typeKey should equal (typeKey[Int])
  }

  // TODO: prop path passes through a collection
  // TODO: prop path specifies a non-basic
  // TODO: nat keys involving assocs
  // TODO: nat keys involving shorthands
  // TODO: prop paths with length > 1

}
