package longevity.unit.subdomain.annotations

import longevity.model.KeyVal
import longevity.model.annotations.keyVal
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** stuff to use in the tests below that need to be stable (ie in packages and objects not in classes */
object KeyValSpec {

  class P

  @keyVal[P] case class KV()

}

/** unit tests for the proper behavior of [[mprops `@keyVal` macro annotation]] */
class KeyValSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "@keyVal"

  it should "cause a compiler error when not supplied a type parameter" in {
    "@keyVal val x = 7"           shouldNot compile
    "@keyVal type X = Int"        shouldNot compile
    "@keyVal def foo = 7"         shouldNot compile
    "def foo(@keyVal x: Int) = 7" shouldNot compile
    "@keyVal trait Foo"           shouldNot compile
    "@keyVal object Foo"          shouldNot compile
    "@keyVal class Foo"           shouldNot compile
  }

  import KeyValSpec._

  it should "cause a compiler error when annotating something other than a class" in {
    "@keyVal[P] val x = 7"           shouldNot compile
    "@keyVal[P] type X = Int"        shouldNot compile
    "@keyVal[P] def foo = 7"         shouldNot compile
    "def foo(@keyVal[P] x: Int) = 7" shouldNot compile
    "@keyVal[P] trait Foo"           shouldNot compile
    "@keyVal[P] object Foo"          shouldNot compile
  }

  it should "create a companion object that extends CType when there is no companion object" in {
    KV().isInstanceOf[KeyVal[P]] should be (true)

    import scala.reflect.runtime.universe.typeOf
    typeOf[KV] <:< typeOf[KeyVal[P]]
  }

}
