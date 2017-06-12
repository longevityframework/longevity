package longevity.unit.model.annotations

import typekey.typeKey
import longevity.model.DerivedCType
import longevity.model.annotations.derivedComponent
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[mprops `@derivedComponent` macro annotation]] */
class DerivedComponentSpec extends FlatSpec with GivenWhenThen with Matchers {

  import testExamples._

  behavior of "@derivedComponent"

  it should "cause a compiler error when annotating something other than a class or object" in {
    "@derivedComponent[DomainModel, PolyComponent] val x = 7"                shouldNot compile
    "@derivedComponent[DomainModel, PolyComponent] type X = Int"             shouldNot compile
    "@derivedComponent[DomainModel, PolyComponent] def foo = 7"              shouldNot compile
    "def foo(@derivedComponent[DomainModel, PolyComponent] x: Int) = 7"      shouldNot compile
    "@derivedComponent[DomainModel, PolyComponent] trait T extends Poly"     shouldNot compile
  }

  it should "create a companion object that extends DerivedCType when there is no companion object" in {
    DerivedComponentNoCompanionClass.isInstanceOf[
      DerivedCType[DomainModel, DerivedComponentNoCompanionClass, PolyComponent]] should be (true)
    DerivedComponentNoCompanionClass.asInstanceOf[
      DerivedCType[DomainModel, DerivedComponentNoCompanionClass, PolyComponent]].cTypeKey should equal {
      typeKey[DerivedComponentNoCompanionClass]
    }
  }

  it should "augment an existing companion object to extend DerivedCType" in {
    DerivedComponentWithCompanion.isInstanceOf[
      DerivedCType[DomainModel, DerivedComponentWithCompanion, PolyComponent]] should be (true)
    DerivedComponentWithCompanion.asInstanceOf[
      DerivedCType[DomainModel, DerivedComponentWithCompanion, PolyComponent]].cTypeKey should equal {
      typeKey[DerivedComponentWithCompanion]
    }
    DerivedComponentWithCompanion.y should equal (7)

    DerivedComponentWithCompanionCaseClass.isInstanceOf[
      DerivedCType[DomainModel, DerivedComponentWithCompanionCaseClass, PolyComponent]] should be (true)
    DerivedComponentWithCompanionCaseClass.asInstanceOf[
      DerivedCType[DomainModel, DerivedComponentWithCompanionCaseClass, PolyComponent]].cTypeKey should equal {
      typeKey[DerivedComponentWithCompanionCaseClass]
    }
    DerivedComponentWithCompanionCaseClass.apply() should equal (DerivedComponentWithCompanionCaseClass())
    DerivedComponentWithCompanionCaseClass.y should equal (7)
  }

  it should "nest the CType when the derivedComponent is already an object" in {
    DerivedComponentNoCompanionObject.ctype.isInstanceOf[
      DerivedCType[DomainModel, DerivedComponentNoCompanionObject.type, PolyComponent]] should be (true)
    DerivedComponentNoCompanionObject.ctype.asInstanceOf[
      DerivedCType[DomainModel, DerivedComponentNoCompanionObject.type, PolyComponent]].cTypeKey should equal {
      typeKey[DerivedComponentNoCompanionObject.type]
    }
  }

}
