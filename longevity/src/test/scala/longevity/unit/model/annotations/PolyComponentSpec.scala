package longevity.unit.model.annotations

import emblem.typeKey
import longevity.model.CType
import longevity.model.annotations.polyComponent
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[mprops `@polyComponent` macro annotation]] */
class PolyComponentSpec extends FlatSpec with GivenWhenThen with Matchers {

  import testExamples._

  behavior of "@polyComponent"

  it should "cause a compiler error when annotating something other than a class, trait, or object" in {
    "@polyComponent[DomainModel] val x = 7"                shouldNot compile
    "@polyComponent[DomainModel] type X = Int"             shouldNot compile
    "@polyComponent[DomainModel] def foo = 7"              shouldNot compile
    "def foo(@polyComponent[DomainModel] x: Int) = 7"      shouldNot compile
    "@polyComponent[DomainModel] class Foo"                shouldNot compile
    "@polyComponent[DomainModel] object Foo"               shouldNot compile
  }

  it should "create a companion object that extends CType when there is no companion object" in {
    PolyComponent.isInstanceOf[CType[DomainModel, PolyComponent]] should be (true)
    PolyComponent.asInstanceOf[CType[DomainModel, PolyComponent]].cTypeKey should equal {
      typeKey[PolyComponent]
    }
  }

  it should "augment an existing companion object to extend CType" in {
    PolyComponentWithCompanion.isInstanceOf[CType[DomainModel, PolyComponentWithCompanion]] should be (true)
    PolyComponentWithCompanion.asInstanceOf[
      CType[DomainModel, PolyComponentWithCompanion]].cTypeKey should equal (typeKey[PolyComponentWithCompanion])
    PolyComponentWithCompanion.y should equal (7)
  }

}
