package longevity.unit.subdomain.annotations

import emblem.typeKey
import longevity.subdomain.DerivedCType
import longevity.subdomain.annotations.derivedComponent
import longevity.subdomain.annotations.polyComponent
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** stuff to use in the tests below that need to be stable (ie in packages and objects not in classes */
object DerivedComponentSpec {

  @polyComponent trait Poly

  @derivedComponent[Poly] class NoCompanionClass extends Poly

  @derivedComponent[Poly] object NoCompanionObject extends Poly

  @derivedComponent[Poly] class WithCompanion extends Poly

  object WithCompanion { val y = 7 }

  @derivedComponent[Poly] case class WithCompanionCaseClass() extends Poly

  object WithCompanionCaseClass { val y = 7 }

}

/** unit tests for the proper behavior of [[mprops `@derivedComponent` macro annotation]] */
class DerivedComponentSpec extends FlatSpec with GivenWhenThen with Matchers {

  import DerivedComponentSpec._

  behavior of "@derivedComponent"

  it should "cause a compiler error when annotating something other than a class or object" in {
    "@derivedComponent[Poly] val x = 7"                shouldNot compile
    "@derivedComponent[Poly] type X = Int"             shouldNot compile
    "@derivedComponent[Poly] def foo = 7"              shouldNot compile
    "def foo(@derivedComponent[Poly] x: Int) = 7"      shouldNot compile
    "@derivedComponent[Poly] trait T extends Poly"     shouldNot compile
  }

  it should "create a companion object that extends CType when there is no companion object" in {
    NoCompanionClass.isInstanceOf[DerivedCType[NoCompanionClass, Poly]] should be (true)
    NoCompanionClass.asInstanceOf[DerivedCType[NoCompanionClass, Poly]].cTypeKey should equal {
      typeKey[NoCompanionClass]
    }
  }

  it should "augment an existing companion object to extend CType" in {
    WithCompanion.isInstanceOf[DerivedCType[WithCompanion, Poly]] should be (true)
    WithCompanion.asInstanceOf[DerivedCType[WithCompanion, Poly]].cTypeKey should equal (typeKey[WithCompanion])
    WithCompanion.y should equal (7)

    WithCompanionCaseClass.isInstanceOf[DerivedCType[WithCompanionCaseClass, Poly]] should be (true)
    WithCompanionCaseClass.asInstanceOf[DerivedCType[WithCompanionCaseClass, Poly]].cTypeKey should equal {
      typeKey[WithCompanionCaseClass]
    }
    WithCompanionCaseClass.apply() should equal (WithCompanionCaseClass())
    WithCompanionCaseClass.y should equal (7)
  }

  it should "nest the CType when the derivedComponent is already an object" in {
    NoCompanionObject.ctype.isInstanceOf[DerivedCType[NoCompanionObject.type, Poly]] should be (true)
    NoCompanionObject.ctype.asInstanceOf[DerivedCType[NoCompanionObject.type, Poly]].cTypeKey should equal {
      typeKey[NoCompanionObject.type]
    }
  }

}
