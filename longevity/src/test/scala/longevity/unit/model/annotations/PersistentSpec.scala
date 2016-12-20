package longevity.unit.model.annotations

import emblem.typeKey
import longevity.model.PType
import longevity.model.annotations.persistent
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** unit tests for the proper behavior of [[mprops `@persistent` macro annotation]] */
class PersistentSpec extends FlatSpec with GivenWhenThen with Matchers {

  import testExamples._

  behavior of "@persistent"

  it should "cause a compiler error when annotating something other than a class, trait, or object" in {
    "@persistent(keySet = Set.empty) val x = 7"           shouldNot compile
    "@persistent(keySet = Set.empty) type X = Int"        shouldNot compile
    "@persistent(keySet = Set.empty) def foo = 7"         shouldNot compile
    "def foo(@persistent(keySet = Set.empty) x: Int) = 7" shouldNot compile
    "@persistent(keySet = Set.empty) trait Foo"           shouldNot compile
    "@persistent(keySet = Set.empty) object Foo"          shouldNot compile
  }

  it should "create a companion object that extends PType when there is no companion object" in {
    PNoCompanion.isInstanceOf[PType[PNoCompanion]] should be (true)
    PNoCompanion.asInstanceOf[PType[PNoCompanion]].pTypeKey should equal {
      typeKey[PNoCompanion]
    }
  }

  it should "augment an existing companion object to extend PType" in {
    PWithCompanion.isInstanceOf[PType[PWithCompanion]] should be (true)
    PWithCompanion.asInstanceOf[PType[PWithCompanion]].pTypeKey should equal {
      typeKey[PWithCompanion]
    }
    PWithCompanion.y should equal (7)

    PCaseClass.isInstanceOf[PType[PCaseClass]] should be (true)

    PCaseClass.asInstanceOf[PType[PCaseClass]].pTypeKey should equal {
      typeKey[PCaseClass]
    }
    PCaseClass.apply() should equal (PCaseClass())

    PCaseClassWithDefaults.isInstanceOf[PType[PCaseClassWithDefaults]] should be (true)

    PCaseClassWithDefaults.asInstanceOf[PType[PCaseClassWithDefaults]].pTypeKey should equal {
      typeKey[PCaseClassWithDefaults]
    }
    PCaseClassWithDefaults.apply(3) should equal (PCaseClassWithDefaults(3))
  }

}
