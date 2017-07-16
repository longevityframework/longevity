package longevity.unit.model

import longevity.exceptions.model.DerivedHasNoPolyException
import longevity.exceptions.model.DuplicateCTypesException
import longevity.exceptions.model.DuplicateKeyException
import longevity.exceptions.model.IndexDuplicatesKeyException
import longevity.exceptions.model.DuplicatePTypesException
import longevity.exceptions.model.InvalidPartitionException
import longevity.exceptions.model.NoSuchPropPathException
import longevity.exceptions.model.PropTypeException
import longevity.exceptions.model.UnsupportedPropTypeException
import longevity.model.DerivedCType
import longevity.model.DerivedPType
import longevity.model.CType
import longevity.model.KVType
import longevity.model.PType
import longevity.model.PolyCType
import longevity.model.PolyPType
import longevity.model.ModelType
import longevity.model.ModelEv
import longevity.model.annotations.pEv
import longevity.model.ptype.Prop
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

package packageScanning {

  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  package nesting {
    case class A(id: String, b: B)
    @pEv object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, String]("id")
      }
    }
  }

  @pEv abstract class FakeoutA extends PType[DomainModel, nesting.A] {
    object props {
      object id extends Prop[FakeoutA, String]("id")
    }
  }

  case class B(name: String)
  object B extends CType[DomainModel, B]

  class FakeoutB extends CType[DomainModel, B]

  object modelType extends ModelType[DomainModel](
    longevity.model.annotations.packscanToList[PType[DomainModel, _]],
    longevity.model.annotations.packscanToList[CType[DomainModel, _]])
}

package emptyPropPath {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: String)
  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, String]("")
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](A :: Nil)
  }
}

package noSuchPropPath {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: String)
  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, String]("noSuchPropPath")
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](A :: Nil)
  }
}

package propTypeWithInternalList {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class B(id: String)

  case class A(id: List[B])
  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, List[B]]("id")
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](A :: Nil, CType[DomainModel, B] :: Nil)
  }
}

package propTypeWithInternalOption {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: Option[B])

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, Option[B]]("id")
    }
  }
  case class B(id: String)

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](A :: Nil, CType[DomainModel, B] :: Nil)
  }
}

package propTypeWithInternalSet {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: Set[B])

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, Set[B]]("id")
    }
  }
  case class B(id: String)

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A), Seq(CType[DomainModel, B]))
  }
}

package noSuchPropPathInComponent {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(b: B)

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, String]("b.noSuchPropPath")
    }
  }
  case class B(id: String)

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A), Seq(CType[DomainModel, B]))
  }
}

package propPathWithTerminalList {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: List[String])

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, List[String]]("id")
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A))
  }
}

package propPathWithTerminalOption {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: Option[String])

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, Option[String]]("id")
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A))
  }
}

package propPathWithTerminalSet {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: Set[String])

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, Set[String]]("id")
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A))
  }
}

package propPathWithTerminalPoly {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  sealed trait B { val id: String }

  case class C(id: String) extends B

  case class A(b: B)

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, B]("b")
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](
      Seq(A),
      Seq(PolyCType[DomainModel, B], DerivedCType[DomainModel, C, B]))
  }
}

package propPathWithInternalList {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: List[B])

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, String]("id.id")
    }
  }
  case class B(id: String)

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A), Seq(CType[DomainModel, B]))
  }
}

package propPathWithInternalOption {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: Option[B])

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, String]("id.id")
    }
  }
  case class B(id: String)

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A), Seq(CType[DomainModel, B]))
  }
}

package propPathWithInternalSet {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: Set[B])

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, String]("id.id")
    }
  }
  case class B(id: String)

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A), Seq(CType[DomainModel, B]))
  }
}

package propPathWithInternalPoly {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  sealed trait B { val id: String }
  case class C(id: String) extends B

  case class A(b: B)

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, String]("b.id")
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](
      Seq(A),
      Seq(PolyCType[DomainModel, B], DerivedCType[DomainModel, C, B]))
    }
}

package incompatiblePropType {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: String)

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, Double]("id")
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A))
  }
}

package supertypePropType {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: String)

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, AnyRef]("id")
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A))
  }
}

package subtypePropType {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  trait AIdSuper
  case class AId(id: String) extends AIdSuper
  object AId extends KVType[DomainModel, A, AId]

  case class A(id: AId)

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, AId]("id")
      object id2 extends Prop[A, AIdSuper]("id") // this is the problematic prop
    }
    implicit val idKey = key(props.id)
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A), Seq(), Seq(AId))
  }
}

package duplicateKey {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class AId(id: String)
  object AId extends KVType[DomainModel, A, AId]

  case class A(id1: AId, id2: AId)

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id1 extends Prop[A, AId]("id1")
      object id2 extends Prop[A, AId]("id2")
    }
    implicit val id1Key = key(props.id1)
    implicit val id2Key = key(props.id2)
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A), Seq(), Seq(AId))
  }
}

package duplicateKeyOrIndex {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class AId(id: String)
  object AId extends KVType[DomainModel, A, AId]

  case class A(id: AId)

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, AId]("id")
    }
    implicit val idKey = key(props.id)
    override val indexSet = Set(index(props.id))
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A), Seq(), Seq(AId))
  }
}

package invalidPartition {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class AId(id1: String, id2: String)
  object AId extends KVType[DomainModel, A, AId]

  case class A(id: AId)

  @pEv object A extends PType[DomainModel, A] {
    object props {
      object id extends Prop[A, AId]("id")
      object id2 extends Prop[A, String]("id.id2")
    }
    implicit val idKey = primaryKey(props.id, partition(props.id2))
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A), Seq(), Seq(AId))
  }
}

package derivedPTypeHasNoPoly {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class Derived(id: String) extends Poly

  @pEv object Derived extends DerivedPType[DomainModel, Derived, Poly] {
    object props {
    }
  }

  sealed trait Poly { val id: String }

  @pEv object Poly extends PolyPType[DomainModel, Poly] {
    object props {
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(Derived))
  }
}

package derivedCTypeHasNoPoly {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  trait Poly { val id: String }
  case class Derived(id: String) extends Poly

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(), Seq(DerivedCType[DomainModel, Derived, Poly]))
  }
}

package duplicateCTypes {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: String)
  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(), Seq(CType[DomainModel, A], CType[DomainModel, A]))
  }
}

package duplicatePTypes {
  trait DomainModel
  object DomainModel {
    implicit object modelEv extends ModelEv[DomainModel]
  }

  case class A(id: String)

  @pEv object A extends PType[DomainModel, A] {
    object props {
    }
  }

  @pEv object B extends PType[DomainModel, A] {
    object props {
    }
  }

  object modelTypeContainer {
    def modelType = new ModelType[DomainModel](Seq(A, B))
  }
}

/** unit tests for the proper [[ModelType]] construction */
class ModelTypeSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "ModelType package scanning"

  it should "collect objects containing PTypes and CTypes" in {
    packageScanning.modelType.pTypePool.size should equal (1)
    packageScanning.modelType.cTypePool.size should equal (1)
  }

  behavior of "ModelType creation"

  it should "throw exception when a PType contains a prop with an empty prop path" in {
    intercept[NoSuchPropPathException] {
      emptyPropPath.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path not found in the persistent type" in {
    intercept[NoSuchPropPathException] {
      noSuchPropPath.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path not found in the component" in {
    intercept[NoSuchPropPathException] {
      noSuchPropPathInComponent.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains a list member" in {
    intercept[UnsupportedPropTypeException] {
      propTypeWithInternalList.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains an option member" in {
    intercept[UnsupportedPropTypeException] {
      propTypeWithInternalOption.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains a set member" in {
    intercept[UnsupportedPropTypeException] {
      propTypeWithInternalSet.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a list" in {
    intercept[UnsupportedPropTypeException] {
      propPathWithTerminalList.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a option" in {
    intercept[UnsupportedPropTypeException] {
      propPathWithTerminalOption.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a set" in {
    intercept[UnsupportedPropTypeException] {
      propPathWithTerminalSet.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a poly" in {
    intercept[UnsupportedPropTypeException] {
      propPathWithTerminalPoly.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary list" in {
    intercept[UnsupportedPropTypeException] {
      propPathWithInternalList.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary option" in {
    intercept[UnsupportedPropTypeException] {
      propPathWithInternalOption.modelTypeContainer.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary set" in {
    intercept[UnsupportedPropTypeException] {
      propPathWithInternalSet.modelTypeContainer.modelType
    }
  }

  // unlike terminal polys, intermediary polys can still be distilled down to a seq of basic components
  it should "pass when a PType contains a prop with a prop path with an intermediary poly" in {
    propPathWithInternalPoly.modelTypeContainer.modelType
  }

  it should "throw exception when the specified prop type is incompatible with the actual type" in {
    intercept[PropTypeException] {
      incompatiblePropType.modelTypeContainer.modelType
    }
  }

  // String <:< AnyVal, but we need requested type to be subtype of the actual type, not the other way around:
  it should "throw exception when the specified prop type is a supertype of the actual type" in {
    intercept[PropTypeException] {
      supertypePropType.modelTypeContainer.modelType
    }
  }

  it should "throw exception when the specified prop type is a subtype of the actual type" in {
    intercept[PropTypeException] {
      subtypePropType.modelTypeContainer.modelType
    }
  }

  it should "throw exception when the PType has multiple keys with the same key value type" in {
    intercept[DuplicateKeyException[_, _]] {
      duplicateKey.modelTypeContainer.modelType
    }
  }

  it should "throw exception when the PType has two or more keys or indexes defined over the same properties" in {
    intercept[IndexDuplicatesKeyException] {
      duplicateKeyOrIndex.modelTypeContainer.modelType
    }
  }

  it should "throw exception if the primary key declares an invalid partition" in {
    intercept[InvalidPartitionException[_]] {
      invalidPartition.modelTypeContainer.modelType
    }
  }

  it should "throw exception when the PolyPType is missing from the PTypePool" in {
    intercept[DerivedHasNoPolyException] {
      derivedPTypeHasNoPoly.modelTypeContainer.modelType
    }
  }

  it should "throw exception when the PolyCType is missing from the CTypePool" in {
    intercept[DerivedHasNoPolyException] {
      derivedCTypeHasNoPoly.modelTypeContainer.modelType
    }
  }

  it should "throw exception when there is a duplicate CType in the CTypePool" in {
    intercept[DuplicateCTypesException] {
      duplicateCTypes.modelTypeContainer.modelType
    }
  }

  it should "throw exception when there is a duplicate PType in the PTypePool" in {
    intercept[DuplicatePTypesException] {
      duplicatePTypes.modelTypeContainer.modelType
    }
  }

}
