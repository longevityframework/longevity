package longevity.unit.model.annotations

import typekey.typeKey
import longevity.model.PolyPType
import longevity.model.annotations.polyPersistent
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[mprops `@polyPersistent` macro annotation]] */
class PolyPersistentSpec extends FlatSpec with GivenWhenThen with Matchers {

  import testExamples._

  behavior of "@polyPersistent"

  // this kind of testing is pretty fruitless, given that even correct cases (like the first,
  // commented out case) do not compile. the combination of the macro annotation and the
  // eval-y thing that ScalaTest is doing is a little too much.
  //
  // given the above, it would make sense to remove these tests, but first, i want to mull over if there
  // is any other reasonable way we could test these error conditions
  it should "cause a compiler error when annotating something other than a class, trait, or object" in {
    //"@polyPersistent[DomainModel] trait T"             should    compile
    "@polyPersistent[DomainModel] val x = 7"           shouldNot compile
    "@polyPersistent[DomainModel] type X = Int"        shouldNot compile
    "@polyPersistent[DomainModel] def foo = 7"         shouldNot compile
    "def foo(@polyPersistent[DomainModel] x: Int) = 7" shouldNot compile
    "@polyPersistent[DomainModel] class Foo"           shouldNot compile
    "@polyPersistent[DomainModel] object Foo"          shouldNot compile
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

    PolyPWithCompanion2.isInstanceOf[PolyPType[DomainModel, PolyPWithCompanion2]] should be (true)
    PolyPWithCompanion2.asInstanceOf[PolyPType[DomainModel, PolyPWithCompanion2]].pTypeKey should equal {
      typeKey[PolyPWithCompanion2]
    }
    PolyPWithCompanion2.y should equal (7)
  }

}
