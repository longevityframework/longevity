package longevity.unit.model.realized

import typekey.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of a [[RealizedKey]] */
class RealizedKeySpec extends FlatSpec with GivenWhenThen with Matchers {

  import longevity.unit.blogCore._

  val userRealizedPType = BlogCore.modelType.realizedPTypes(User)
  val realizedKey = userRealizedPType.realizedKey[Username]

  behavior of "RealizedKey.keyValTypeKey"
  it should "produce the appropriate type key for the key val type" in {
    realizedKey.keyValTypeKey should equal (typeKey[Username])
  }

  behavior of "RealizedKey.keyValForP"
  it should "produce the appropriate key value for the supplied persistent" in {
    val username = Username("username77")
    val user = User(username, Email("email"), "fullname")
    realizedKey.keyValForP(user) should equal (username)
  }

  behavior of "RealizedKey.updateKeyVal"
  it should "produce a copy of the persistent with the new keyval" in {
    val username1 = Username("username77")
    val username2 = Username("username999")
    val user = User(username1, Email("email"), "fullname")
    realizedKey.updateKeyVal(user, username2) should equal (user.copy(username = username2))
  }

  behavior of "RealizedKey.toString"
  it should "produce a readable string containing type names for the persistent and the keyval" in {
    realizedKey.toString should equal ("RealizedKey[User,Username]")
  }

}
