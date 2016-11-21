package longevity.unit.subdomain.annotations

import emblem.typeKey
import longevity.subdomain.CType
import longevity.subdomain.annotations.component
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** stuff to use in the tests below that need to be stable (ie in packages and objects not in classes */
object ComponentSpec {

  @component class NoCompanionClass

  @component object NoCompanionObject

  @component trait NoCompanionTrait

  @component class WithCompanion

  object WithCompanion { val y = 7 }

  @component case class WithCompanionCaseClass()

  // TODO companion object already extends class other than AnyRef

}

/** unit tests for the proper behavior of [[mprops `@component` macro annotation]] */
class ComponentSpec extends FlatSpec with GivenWhenThen with Matchers {

  import ComponentSpec._

  behavior of "@component"

  it should "cause a compiler error when annotating something other than a class, trait, or object" in {
    "@component val x = 7"                shouldNot compile
    "@component type X = Int"             shouldNot compile
    "@component def foo = 7"              shouldNot compile
    "def foo(@component x: Int) = 7"      shouldNot compile
  }

  it should "create a companion object that extends CType when there is no companion object" in {
    NoCompanionClass.isInstanceOf[CType[NoCompanionClass]] should be (true)
    NoCompanionClass.asInstanceOf[CType[NoCompanionClass]].cTypeKey should equal (typeKey[NoCompanionClass])

    NoCompanionTrait.isInstanceOf[CType[NoCompanionTrait]] should be (true)
    NoCompanionTrait.asInstanceOf[CType[NoCompanionTrait]].cTypeKey should equal (typeKey[NoCompanionTrait])
  }

  it should "augment an existing companion object to extend CType" in {
    WithCompanion.isInstanceOf[CType[WithCompanion]] should be (true)
    WithCompanion.asInstanceOf[CType[WithCompanion]].cTypeKey should equal (typeKey[WithCompanion])
    WithCompanion.y should equal (7)

    WithCompanionCaseClass.isInstanceOf[CType[WithCompanionCaseClass]] should be (true)
    WithCompanionCaseClass.asInstanceOf[CType[WithCompanionCaseClass]].cTypeKey should equal {
      typeKey[WithCompanionCaseClass]
    }
    WithCompanionCaseClass.apply() should equal (WithCompanionCaseClass())
  }

  it should "next the CType when the component is already an object" in {
    NoCompanionObject.ctype.isInstanceOf[CType[NoCompanionObject.type]] should be (true)
    NoCompanionObject.ctype.asInstanceOf[CType[NoCompanionObject.type]].cTypeKey should equal {
      typeKey[NoCompanionObject.type]
    }
  }

}
