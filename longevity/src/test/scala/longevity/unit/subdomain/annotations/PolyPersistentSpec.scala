package longevity.unit.subdomain.annotations

import emblem.typeKey
import longevity.model.PolyPType
import longevity.model.annotations.polyPersistent
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[mprops `@polyPersistent` macro annotation]] */
class PolyPersistentSpec extends FlatSpec with GivenWhenThen with Matchers {

  import testExamples._

  behavior of "@polyPersistent"

  it should "cause a compiler error when annotating something other than a class, trait, or object" in {
    "@polyPersistent val x = 7"           shouldNot compile
    "@polyPersistent type X = Int"        shouldNot compile
    "@polyPersistent def foo = 7"         shouldNot compile
    "def foo(@polyPersistent x: Int) = 7" shouldNot compile
    "@polyPersistent class Foo"           shouldNot compile
    "@polyPersistent object Foo"          shouldNot compile

    "@polyPersistent(keySet = Set.empty) val x = 7"           shouldNot compile
    "@polyPersistent(keySet = Set.empty) type X = Int"        shouldNot compile
    "@polyPersistent(keySet = Set.empty) def foo = 7"         shouldNot compile
    "def foo(@polyPersistent(keySet = Set.empty) x: Int) = 7" shouldNot compile
    "@polyPersistent(keySet = Set.empty) class Foo"           shouldNot compile
    "@polyPersistent(keySet = Set.empty) object Foo"          shouldNot compile
  }

  it should "create a companion object that extends PolyPType when there is no companion object" in {
    PolyPNoCompanion.isInstanceOf[PolyPType[PolyPNoCompanion]] should be (true)
    PolyPNoCompanion.asInstanceOf[PolyPType[PolyPNoCompanion]].pTypeKey should equal {
      typeKey[PolyPNoCompanion]
    }
  }

  it should "augment an existing companion object to extend PolyPType" in {
    PolyPWithCompanion.isInstanceOf[PolyPType[PolyPWithCompanion]] should be (true)
    PolyPWithCompanion.asInstanceOf[PolyPType[PolyPWithCompanion]].pTypeKey should equal {
      typeKey[PolyPWithCompanion]
    }
    PolyPWithCompanion.y should equal (7)
  }

}
