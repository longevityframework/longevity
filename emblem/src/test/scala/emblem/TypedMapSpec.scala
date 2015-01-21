package emblem

import org.scalatest._
import org.scalatest.OptionValues._

// TODO some semi-realistic examples would be helpful here

/** [[TypedMap]] specifications */
class TypedMapSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "a TypedMap where the type bound, key, and value types are all the same"

  private sealed trait A
  private case class A1(i: Int) extends A
  private case class A2(i: Int) extends A
  private case class A3(i: Int) extends A
  private type Identity[AN <: A] = AN

  it should "only allow key/value pairs with matching type param" in {
    var map = TypedMap[A, Identity, Identity]()
    "map += typeKey[A1] -> A1(1) -> A1(2)" should compile
    "map += typeKey[A1] -> A1(1) -> A2(2)" shouldNot compile
  }

  it should "store multiple key/value pairs for a given type param" in {
    var map = TypedMap[A, Identity, Identity]()
    map += typeKey[A1] -> A1(2) -> A1(0)
    map += typeKey[A1] -> A1(2) -> A1(4) // overwrites val A1(0)
    map += typeKey[A1] -> A1(3) -> A1(9)
    map(A1(2)) should equal (A1(4))
    map(A1(3)) should equal (A1(9))
  }

  // TODO: different, single-TP key/value types
  // TODO: single-TP key type, double-TP value type
  // TODO: double-TP key type, single-TP value type
  // TODO: specs to exercise api

}
