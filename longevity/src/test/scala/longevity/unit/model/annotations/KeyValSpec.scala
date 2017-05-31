package longevity.unit.model.annotations

import longevity.model.annotations.keyVal
import longevity.model.KVType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[mprops `@keyVal` macro annotation]] */
class KeyValSpec extends FlatSpec with GivenWhenThen with Matchers {

  import keyValExample._

  behavior of "@keyVal"

  it should "cause a compiler error when no type parameters" in {
    "@keyVal val x = 7"           shouldNot compile
    "@keyVal type X = Int"        shouldNot compile
    "@keyVal def foo = 7"         shouldNot compile
    "def foo(@keyVal x: Int) = 7" shouldNot compile
    "@keyVal trait Foo"           shouldNot compile
    "@keyVal object Foo"          shouldNot compile
    "@keyVal class Foo"           shouldNot compile
  }

  it should "cause a compiler error when annotating something other than a class" in {
    "@keyVal[M, P] val x = 7"           shouldNot compile
    "@keyVal[M, P] type X = Int"        shouldNot compile
    "@keyVal[M, P] def foo = 7"         shouldNot compile
    "def foo(@keyVal[M, P] x: Int) = 7" shouldNot compile
    "@keyVal[M, P] trait Foo"           shouldNot compile
    "@keyVal[M, P] object Foo"          shouldNot compile
  }

  it should "create a companion object that extends KVType when there is no companion object" in {
    KV.isInstanceOf[KVType[M, P, KV]] should be (true)
  }

}
