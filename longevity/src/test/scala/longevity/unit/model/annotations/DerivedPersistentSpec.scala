package longevity.unit.model.annotations

import emblem.typeKey
import longevity.model.DerivedPType
import longevity.model.annotations.derivedPersistent
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[mprops `@derivedPersistent` macro annotation]] */
class DerivedPersistentSpec extends FlatSpec with GivenWhenThen with Matchers {

  import testExamples._

  behavior of "@derivedPersistent"

  it should "cause a compiler error when annotating something other than a class or object" in {
    "@derivedPersistent[Poly] val x = 7"                shouldNot compile
    "@derivedPersistent[Poly] type X = Int"             shouldNot compile
    "@derivedPersistent[Poly] def foo = 7"              shouldNot compile
    "def foo(@derivedPersistent[Poly] x: Int) = 7"      shouldNot compile
    "@derivedPersistent[Poly] trait T extends Poly"     shouldNot compile
  }

  it should "create a companion object that extends DerivedPType when there is no companion object" in {
    DerivedPNoCompanion.isInstanceOf[DerivedPType[DerivedPNoCompanion, Poly]] should be (true)
    DerivedPNoCompanion.asInstanceOf[DerivedPType[DerivedPNoCompanion, Poly]].pTypeKey should equal {
      typeKey[DerivedPNoCompanion]
    }
  }

  it should "augment an existing companion object to extend DerivedPType" in {
    DerivedPWithCompanion.isInstanceOf[DerivedPType[DerivedPWithCompanion, Poly]] should be (true)
    DerivedPWithCompanion.asInstanceOf[DerivedPType[DerivedPWithCompanion, Poly]].pTypeKey should equal {
      typeKey[DerivedPWithCompanion]
    }
    DerivedPWithCompanion.y should equal (7)

    DerivedPCaseClass.isInstanceOf[DerivedPType[DerivedPCaseClass, Poly]] should be (true)
    DerivedPCaseClass.asInstanceOf[DerivedPType[DerivedPCaseClass, Poly]].pTypeKey should equal {
      typeKey[DerivedPCaseClass]
    }
    DerivedPCaseClass.apply() should equal (DerivedPCaseClass())
    DerivedPCaseClass.y should equal (7)
  }

}
