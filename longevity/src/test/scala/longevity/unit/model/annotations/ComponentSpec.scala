package longevity.unit.model.annotations

import emblem.typeKey
import longevity.model.annotations.component
import longevity.model.CType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[mprops `@component` macro annotation]] */
class ComponentSpec extends FlatSpec with GivenWhenThen with Matchers {

  import testExamples._

  behavior of "@component"

  it should "cause a compiler error when annotating something other than a class" in {
    "@component[DomainModel] val x = 7"           shouldNot compile
    "@component[DomainModel] type X = Int"        shouldNot compile
    "@component[DomainModel] def foo = 7"         shouldNot compile
    "def foo(@component[DomainModel] x: Int) = 7" shouldNot compile
    "@component[DomainModel] trait Foo"           shouldNot compile
    "@component[DomainModel] object Foo"          shouldNot compile
  }

  it should "create a companion object that extends CType when there is no companion object" in {
    ComponentNoCompanionClass.isInstanceOf[CType[DomainModel, ComponentNoCompanionClass]] should be (true)
    ComponentNoCompanionClass.asInstanceOf[
      CType[DomainModel, ComponentNoCompanionClass]].cTypeKey should equal (typeKey[ComponentNoCompanionClass])
  }

  it should "augment an existing companion object to extend CType" in {
    ComponentWithCompanion.isInstanceOf[CType[DomainModel, ComponentWithCompanion]] should be (true)
    ComponentWithCompanion.asInstanceOf[
      CType[DomainModel, ComponentWithCompanion]].cTypeKey should equal (typeKey[ComponentWithCompanion])
    ComponentWithCompanion.y should equal (7)

    ComponentWithCompanionCaseClass.isInstanceOf[CType[DomainModel, ComponentWithCompanionCaseClass]] should be (true)
    ComponentWithCompanionCaseClass.asInstanceOf[CType[DomainModel, ComponentWithCompanionCaseClass]].cTypeKey should equal {
      typeKey[ComponentWithCompanionCaseClass]
    }
    ComponentWithCompanionCaseClass.apply() should equal (ComponentWithCompanionCaseClass())
  }

}
