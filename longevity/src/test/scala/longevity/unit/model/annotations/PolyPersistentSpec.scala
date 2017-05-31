package longevity.unit.model.annotations

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
    "@polyPersistent[DomainModel](keySet = Set.empty) val x = 7"           shouldNot compile
    "@polyPersistent[DomainModel](keySet = Set.empty) type X = Int"        shouldNot compile
    "@polyPersistent[DomainModel](keySet = Set.empty) def foo = 7"         shouldNot compile
    "def foo(@polyPersistent[DomainModel](keySet = Set.empty) x: Int) = 7" shouldNot compile
    "@polyPersistent[DomainModel](keySet = Set.empty) class Foo"           shouldNot compile
    "@polyPersistent[DomainModel](keySet = Set.empty) object Foo"          shouldNot compile
  }

  it should "create a companion object that extends PolyPType when there is no companion object" in {
    PolyPNoCompanion.isInstanceOf[PolyPType[DomainModel, PolyPNoCompanion]] should be (true)
    PolyPNoCompanion.asInstanceOf[PolyPType[DomainModel, PolyPNoCompanion]].pTypeKey should equal {
      typeKey[PolyPNoCompanion]
    }
  }

  it should "augment an existing companion object to extend PolyPType" in {
    PolyPWithCompanion.isInstanceOf[PolyPType[DomainModel, PolyPWithCompanion]] should be (true)
    PolyPWithCompanion.asInstanceOf[PolyPType[DomainModel, PolyPWithCompanion]].pTypeKey should equal {
      typeKey[PolyPWithCompanion]
    }
    PolyPWithCompanion.y should equal (7)
  }

}
