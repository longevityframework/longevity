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
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, String]("id")
      }
    }
  }

  abstract class FakeoutA extends PType[DomainModel, nesting.A] {
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

/** holds factory methods for sample modelTypes used in [[ModelTypeSpec]] */
object ModelTypeSpec {

  object emptyPropPath {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: String)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, String]("")
      }
    }

    def modelType = new ModelType[DomainModel](A :: Nil)
  }

  object noSuchPropPath {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: String)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, String]("noSuchPropPath")
      }
    }

    def modelType = new ModelType[DomainModel](A :: Nil)
  }

  object propTypeWithInternalList {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: List[B])
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, List[B]]("id")
      }
    }
    case class B(id: String)

    def modelType = new ModelType[DomainModel](A :: Nil, CType[DomainModel, B] :: Nil)
  }

  object propTypeWithInternalOption {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: Option[B])
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, Option[B]]("id")
      }
    }
    case class B(id: String)

    def modelType = new ModelType[DomainModel](A :: Nil, CType[DomainModel, B] :: Nil)
  }

  object propTypeWithInternalSet {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: Set[B])
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, Set[B]]("id")
      }
    }
    case class B(id: String)

    def modelType = new ModelType[DomainModel](Seq(A), Seq(CType[DomainModel, B]))
  }

  object propTypeWithInternalPoly {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(b: B)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, B]("b")
      }
    }

    trait B { val id: String }
    case class C(id: String) extends B

    def modelType = new ModelType[DomainModel](
      Seq(A),
      Seq(PolyCType[DomainModel, B], DerivedCType[DomainModel, C, B]))
  }

  object noSuchPropPathInComponent {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(b: B)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, String]("b.noSuchPropPath")
      }
    }
    case class B(id: String)

    def modelType = new ModelType[DomainModel](Seq(A), Seq(CType[DomainModel, B]))
  }

  object propPathWithNonEmbeddable {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    import java.util.UUID
    case class A(id: UUID)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, UUID]("id")
      }
    }

    def modelType = new ModelType[DomainModel](Seq(A))
  }

  object propPathWithTerminalList {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: List[String])
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, List[String]]("id")
      }
    }

    def modelType = new ModelType[DomainModel](Seq(A))
  }

  object propPathWithTerminalOption {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: Option[String])
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, Option[String]]("id")
      }
    }

    def modelType = new ModelType[DomainModel](Seq(A))
  }

  object propPathWithTerminalSet {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: Set[String])
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, Set[String]]("id")
      }
    }

    def modelType = new ModelType[DomainModel](Seq(A))
  }

  object propPathWithTerminalPoly {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(b: B)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, B]("b")
      }
    }

    trait B { val id: String }

    case class C(id: String) extends B

    def modelType = new ModelType[DomainModel](
      Seq(A),
      Seq(PolyCType[DomainModel, B], DerivedCType[DomainModel, C, B]))
  }

  object propPathWithInternalList {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: List[B])
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, String]("id.id")
      }
    }
    case class B(id: String)

    def modelType = new ModelType[DomainModel](Seq(A), Seq(CType[DomainModel, B]))
  }

  object propPathWithInternalOption {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: Option[B])
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, String]("id.id")
      }
    }
    case class B(id: String)

    def modelType = new ModelType[DomainModel](Seq(A), Seq(CType[DomainModel, B]))
  }

  object propPathWithInternalSet {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: Set[B])
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, String]("id.id")
      }
    }
    case class B(id: String)

    def modelType = new ModelType[DomainModel](Seq(A), Seq(CType[DomainModel, B]))
  }

  object propPathWithInternalPoly {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(b: B)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, String]("b.id")
      }
    }

    trait B { val id: String }
    case class C(id: String) extends B

    def modelType = new ModelType[DomainModel](
      Seq(A),
      Seq(PolyCType[DomainModel, B], DerivedCType[DomainModel, C, B]))
  }

  object incompatiblePropType {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: String)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, Double]("id")
      }
    }

    def modelType = new ModelType[DomainModel](Seq(A))
  }

  object supertypePropType {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: String)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, AnyRef]("id")
      }
    }

    def modelType = new ModelType[DomainModel](Seq(A))
  }

  object subtypePropType {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    trait AIdSuper
    case class AId(id: String) extends AIdSuper
    object AId extends KVType[DomainModel, A, AId]

    case class A(id: AId)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, AId]("id")
        object id2 extends Prop[A, AIdSuper]("id") // this is the problematic prop
      }
      implicit val idKey = key(props.id)
    }

    def modelType = new ModelType[DomainModel](Seq(A), Seq(), Seq(AId))
  }

  object duplicateKey {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class AId(id: String)
    object AId extends KVType[DomainModel, A, AId]

    case class A(id1: AId, id2: AId)
    object A extends PType[DomainModel, A] {
      object props {
        object id1 extends Prop[A, AId]("id1")
        object id2 extends Prop[A, AId]("id2")
      }
      implicit val id1Key = key(props.id1)
      implicit val id2Key = key(props.id2)
    }

    def modelType = new ModelType[DomainModel](Seq(A), Seq(), Seq(AId))
  }

  object duplicateKeyOrIndex {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class AId(id: String)
    object AId extends KVType[DomainModel, A, AId]

    case class A(id: AId)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, AId]("id")
      }
      implicit val idKey = key(props.id)
      override val indexSet = Set(index(props.id))
    }

    def modelType = new ModelType[DomainModel](Seq(A), Seq(), Seq(AId))
  }

  object invalidPartition {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class AId(id1: String, id2: String)
    object AId extends KVType[DomainModel, A, AId]

    case class A(id: AId)
    object A extends PType[DomainModel, A] {
      object props {
        object id extends Prop[A, AId]("id")
        object id2 extends Prop[A, String]("id.id2")
      }
      implicit val idKey = primaryKey(props.id, partition(props.id2))
    }

    def modelType = new ModelType[DomainModel](Seq(A), Seq(), Seq(AId))
  }

  object derivedPTypeHasNoPoly {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    trait Poly { val id: String }
    object Poly extends PolyPType[DomainModel, Poly] {
      object props {
      }
    }

    case class Derived(id: String) extends Poly
    object Derived extends DerivedPType[DomainModel, Derived, Poly] {
      object props {
      }
    }

    def modelType = new ModelType[DomainModel](Seq(Derived))
  }

  object derivedCTypeHasNoPoly {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    trait Poly { val id: String }
    case class Derived(id: String) extends Poly

    def modelType = new ModelType[DomainModel](Seq(), Seq(DerivedCType[DomainModel, Derived, Poly]))
  }

  object duplicateCTypes {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: String)
    def modelType = new ModelType[DomainModel](Seq(), Seq(CType[DomainModel, A], CType[DomainModel, A]))
  }

  object duplicatePTypes {
    trait DomainModel
    object DomainModel {
      implicit object modelEv extends ModelEv[DomainModel]
    }

    case class A(id: String)
    object A extends PType[DomainModel, A] {
      object props {
      }
    }
    object B extends PType[DomainModel, A] {
      object props {
      }
    }

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
      ModelTypeSpec.emptyPropPath.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path not found in the persistent type" in {
    intercept[NoSuchPropPathException] {
      ModelTypeSpec.noSuchPropPath.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path not found in the component" in {
    intercept[NoSuchPropPathException] {
      ModelTypeSpec.noSuchPropPathInComponent.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains a list member" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propTypeWithInternalList.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains an option member" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propTypeWithInternalOption.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains a set member" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propTypeWithInternalSet.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains a poly member" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propTypeWithInternalPoly.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a non-embeddable, non-collection, non-basic" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propPathWithNonEmbeddable.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a list" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propPathWithTerminalList.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a option" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propPathWithTerminalOption.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a set" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propPathWithTerminalSet.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a poly" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propPathWithTerminalPoly.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary list" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propPathWithInternalList.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary option" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propPathWithInternalOption.modelType
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary set" in {
    intercept[UnsupportedPropTypeException] {
      ModelTypeSpec.propPathWithInternalSet.modelType
    }
  }

  // unlike terminal polys, intermediary polys can still be distilled down to a seq of basic components
  it should "pass when a PType contains a prop with a prop path with an intermediary poly" in {
    ModelTypeSpec.propPathWithInternalPoly.modelType
  }

  it should "throw exception when the specified prop type is incompatible with the actual type" in {
    intercept[PropTypeException] {
      ModelTypeSpec.incompatiblePropType.modelType
    }
  }

  // String <:< AnyVal, but we need requested type to be subtype of the actual type, not the other way around:
  it should "throw exception when the specified prop type is a supertype of the actual type" in {
    intercept[PropTypeException] {
      ModelTypeSpec.supertypePropType.modelType
    }
  }

  it should "throw exception when the specified prop type is a subtype of the actual type" in {
    intercept[PropTypeException] {
      ModelTypeSpec.subtypePropType.modelType
    }
  }

  it should "throw exception when the PType has multiple keys with the same key value type" in {
    intercept[DuplicateKeyException[_, _]] {
      ModelTypeSpec.duplicateKey.modelType
    }
  }

  it should "throw exception when the PType has two or more keys or indexes defined over the same properties" in {
    intercept[IndexDuplicatesKeyException] {
      ModelTypeSpec.duplicateKeyOrIndex.modelType
    }
  }

  it should "throw exception if the primary key declares an invalid partition" in {
    intercept[InvalidPartitionException[_]] {
      ModelTypeSpec.invalidPartition.modelType
    }
  }

  it should "throw exception when the PolyPType is missing from the PTypePool" in {
    intercept[DerivedHasNoPolyException] {
      ModelTypeSpec.derivedPTypeHasNoPoly.modelType
    }
  }

  it should "throw exception when the PolyCType is missing from the CTypePool" in {
    intercept[DerivedHasNoPolyException] {
      ModelTypeSpec.derivedCTypeHasNoPoly.modelType
    }
  }

  it should "throw exception when there is a duplicate CType in the CTypePool" in {
    intercept[DuplicateCTypesException] {
      ModelTypeSpec.duplicateCTypes.modelType
    }
  }

  it should "throw exception when there is a duplicate PType in the PTypePool" in {
    intercept[DuplicatePTypesException] {
      ModelTypeSpec.duplicatePTypes.modelType
    }
  }

}
