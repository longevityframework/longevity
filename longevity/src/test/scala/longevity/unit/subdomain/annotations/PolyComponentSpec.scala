package longevity.unit.subdomain.annotations

import emblem.typeKey
import longevity.subdomain.CType
import longevity.subdomain.annotations.polyComponent
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** stuff to use in the tests below that need to be stable (ie in packages and objects not in classes */
object PolyComponentSpec {

  @polyComponent trait NoCompanionTrait

  @polyComponent trait WithCompanion

  object WithCompanion { val y = 7 }

}

/** unit tests for the proper behavior of [[mprops `@polyComponent` macro annotation]] */
class PolyComponentSpec extends FlatSpec with GivenWhenThen with Matchers {

  import PolyComponentSpec._

  behavior of "@polyComponent"

  it should "cause a compiler error when annotating something other than a class, trait, or object" in {
    "@polyComponent val x = 7"                shouldNot compile
    "@polyComponent type X = Int"             shouldNot compile
    "@polyComponent def foo = 7"              shouldNot compile
    "def foo(@polyComponent x: Int) = 7"      shouldNot compile
    "@polyComponent class Foo"                shouldNot compile
    "@polyComponent object Foo"               shouldNot compile
  }

  it should "create a companion object that extends CType when there is no companion object" in {
    NoCompanionTrait.isInstanceOf[CType[NoCompanionTrait]] should be (true)
    NoCompanionTrait.asInstanceOf[CType[NoCompanionTrait]].cTypeKey should equal (typeKey[NoCompanionTrait])
  }

  it should "augment an existing companion object to extend CType" in {
    WithCompanion.isInstanceOf[CType[WithCompanion]] should be (true)
    WithCompanion.asInstanceOf[CType[WithCompanion]].cTypeKey should equal (typeKey[WithCompanion])
    WithCompanion.y should equal (7)
  }

}
