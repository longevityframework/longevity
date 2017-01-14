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
import longevity.model.CTypePool
import longevity.model.KeyVal
import longevity.model.PType
import longevity.model.PTypePool
import longevity.model.PolyCType
import longevity.model.PolyPType
import longevity.model.DomainModel
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

package packageScanning {

  package nesting {
    case class A(id: String, b: B)
    object A extends PType[A] {
      object props {
        val id = prop[String]("id")
      }
      val keySet = emptyKeySet
    }
  }

  abstract class FakeoutA extends PType[nesting.A] {
    object props {
      val id = prop[String]("id")
    }
    val keySet = emptyKeySet
  }

  case class B(name: String)
  object B extends CType[B]

  class FakeoutB extends CType[B]

  object domainModel extends DomainModel("longevity.unit.model.packageScanning")
}

/** holds factory methods for sample domainModels used in [[DomainModelSpec]] */
object DomainModelSpec {

  object emptyPropPath {
    case class A(id: String)
    object A extends PType[A] {
      object props {
        val id = prop[String]("")
      }
      val keySet = emptyKeySet
    }
    def domainModel = DomainModel(PTypePool(A))
  }

  object noSuchPropPath {
    case class A(id: String)
    object A extends PType[A] {
      object props {
        val id = prop[String]("noSuchPropPath")
      }
      val keySet = emptyKeySet
    }
    def domainModel = DomainModel(PTypePool(A))
  }

  object propTypeWithInternalList {
    case class A(id: List[B])
    object A extends PType[A] {
      object props {
        val id = prop[A]("id")
      }
      val keySet = emptyKeySet
    }
    case class B(id: String)
    def domainModel = DomainModel(PTypePool(A), CTypePool(CType[B]))
  }

  object propTypeWithInternalOption {
    case class A(id: Option[B])
    object A extends PType[A] {
      object props {
        val id = prop[A]("id")
      }
      val keySet = emptyKeySet
    }
    case class B(id: String)
    def domainModel = DomainModel(PTypePool(A), CTypePool(CType[B]))
  }

  object propTypeWithInternalSet {
    case class A(id: Set[B])
    object A extends PType[A] {
      object props {
        val id = prop[A]("id")
      }
      val keySet = emptyKeySet
    }
    case class B(id: String)
    def domainModel = DomainModel(PTypePool(A), CTypePool(CType[B]))
  }

  object propTypeWithInternalPoly {
    case class A(b: B)
    object A extends PType[A] {
      object props {
        val id = prop[B]("b")
      }
      val keySet = emptyKeySet
    }

    trait B { val id: String }
    case class C(id: String) extends B

    def domainModel = DomainModel(
      PTypePool(A),
      CTypePool(PolyCType[B], DerivedCType[C, B]))
  }

  object noSuchPropPathInComponent {
    case class A(b: B)
    object A extends PType[A] {
      object props {
        val id = prop[String]("b.noSuchPropPath")
      }
      val keySet = emptyKeySet
    }
    case class B(id: String)
    def domainModel = DomainModel(PTypePool(A), CTypePool(CType[B]))
  }

  object propPathWithNonEmbeddable {
    import java.util.UUID
    case class A(id: UUID)
    object A extends PType[A] {
      object props {
        val id = prop[UUID]("id")
      }
      val keySet = emptyKeySet
    }
    def domainModel = DomainModel(PTypePool(A))
  }

  object propPathWithTerminalList {
    case class A(id: List[String])
    object A extends PType[A] {
      object props {
        val id = prop[List[String]]("id")
      }
      val keySet = emptyKeySet
    }
    def domainModel = DomainModel(PTypePool(A))
  }

  object propPathWithTerminalOption {
    case class A(id: Option[String])
    object A extends PType[A] {
      object props {
        val id = prop[Option[String]]("id")
      }
      val keySet = emptyKeySet
    }
    def domainModel = DomainModel(PTypePool(A))
  }

  object propPathWithTerminalSet {
    case class A(id: Set[String])
    object A extends PType[A] {
      object props {
        val id = prop[Set[String]]("id")
      }
      val keySet = emptyKeySet
    }
    def domainModel = DomainModel(PTypePool(A))
  }

  object propPathWithTerminalPoly {
    case class A(b: B)
    object A extends PType[A] {
      object props {
        val id = prop[B]("b")
      }
      val keySet = emptyKeySet
    }

    trait B { val id: String }

    case class C(id: String) extends B

    def domainModel = DomainModel(
      PTypePool(A),
      CTypePool(PolyCType[B], DerivedCType[C, B]))
  }

  object propPathWithInternalList {
    case class A(id: List[B])
    object A extends PType[A] {
      object props {
        val id = prop[String]("id.id")
      }
      val keySet = emptyKeySet
    }
    case class B(id: String)
    def domainModel = DomainModel(PTypePool(A), CTypePool(CType[B]))
  }

  object propPathWithInternalOption {
    case class A(id: Option[B])
    object A extends PType[A] {
      object props {
        val id = prop[String]("id.id")
      }
      val keySet = emptyKeySet
    }
    case class B(id: String)
    def domainModel = DomainModel(PTypePool(A), CTypePool(CType[B]))
  }

  object propPathWithInternalSet {
    case class A(id: Set[B])
    object A extends PType[A] {
      object props {
        val id = prop[String]("id.id")
      }
      val keySet = emptyKeySet
    }
    case class B(id: String)
    def domainModel = DomainModel(PTypePool(A), CTypePool(CType[B]))
  }

  object propPathWithInternalPoly {
    case class A(b: B)
    object A extends PType[A] {
      object props {
        val id = prop[String]("b.id")
      }
      val keySet = emptyKeySet
    }

    trait B { val id: String }
    case class C(id: String) extends B

    def domainModel = DomainModel(
      PTypePool(A),
      CTypePool(PolyCType[B], DerivedCType[C, B]))
  }

  object incompatiblePropType {
    case class A(id: String)
    object A extends PType[A] {
      object props {
        val id = prop[Double]("id")
      }
      val keySet = emptyKeySet
    }
    def domainModel = DomainModel(PTypePool(A))
  }

  object supertypePropType {
    case class A(id: String)
    object A extends PType[A] {
      object props {
        val id = prop[AnyRef]("id")
      }
      val keySet = emptyKeySet
    }
    def domainModel = DomainModel(PTypePool(A))
  }

  object subtypePropType {

    case class AId(id: String) extends KeyVal[A]

    case class A(id: AId)
    object A extends PType[A] {
      object props {
        val id = prop[AId]("id")
        val id2 = prop[KeyVal[A]]("id") // this is the problematic prop
      }
      val keySet = Set(key(props.id))
    }

    def domainModel = DomainModel(PTypePool(A))
  }

  object duplicateKey {

    case class AId(id: String) extends KeyVal[A]

    case class A(id1: AId, id2: AId)
    object A extends PType[A] {
      object props {
        val id1 = prop[AId]("id1")
        val id2 = prop[AId]("id2")
      }
      val keySet = Set(key(props.id1), key(props.id2))
    }

    def domainModel = DomainModel(PTypePool(A))

  }

  object duplicateKeyOrIndex {

    case class AId(id: String) extends KeyVal[A]

    case class A(id: AId)
    object A extends PType[A] {
      object props {
        val id = prop[AId]("id")
      }
      val keySet = Set(key(props.id))
      override val indexSet = Set(index(props.id))
    }

    def domainModel = DomainModel(PTypePool(A))

  }

  object invalidPartition {

    case class AId(id1: String, id2: String) extends KeyVal[A]

    case class A(id: AId)
    object A extends PType[A] {
      object props {
        val id = prop[AId]("id")
        val id2 = prop[String]("id.id2")
      }
      val keySet = Set(primaryKey(props.id, partition(props.id2)))
    }

    def domainModel = DomainModel(PTypePool(A))

  }

  object derivedPTypeHasNoPoly {

    trait Poly { val id: String }
    object Poly extends PolyPType[Poly] {
      object props {
      }
      val keySet = emptyKeySet
    }

    case class Derived(id: String) extends Poly
    object Derived extends DerivedPType[Derived, Poly] {
      object props {
      }
      val keySet = emptyKeySet
    }

    def domainModel = DomainModel(PTypePool(Derived))
  }

  object derivedCTypeHasNoPoly {
    trait Poly { val id: String }
    case class Derived(id: String) extends Poly
    def domainModel = DomainModel(PTypePool(), CTypePool(DerivedCType[Derived, Poly]))
  }

  object duplicateCTypes {
    case class A(id: String)
    def domainModel = DomainModel(PTypePool(), CTypePool(CType[A], CType[A]))
  }

  object duplicatePTypes {
    case class A(id: String)
    object A extends PType[A] {
      object props {
      }
      val keySet = emptyKeySet
    }
    object B extends PType[A] {
      object props {
      }
      val keySet = emptyKeySet
    }
    def domainModel = DomainModel(PTypePool(A, B))
  }

}

/** unit tests for the proper [[DomainModel]] construction */
class DomainModelSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "DomainModel package scanning"

  it should "collect objects containing PTypes and CTypes" in {
    packageScanning.domainModel.pTypePool.size should equal (1)
    packageScanning.domainModel.cTypePool.size should equal (1)
  }

  behavior of "DomainModel creation"

  it should "throw exception when a PType contains a prop with an empty prop path" in {
    intercept[NoSuchPropPathException] {
      DomainModelSpec.emptyPropPath.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a prop path not found in the persistent type" in {
    intercept[NoSuchPropPathException] {
      DomainModelSpec.noSuchPropPath.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a prop path not found in the component" in {
    intercept[NoSuchPropPathException] {
      DomainModelSpec.noSuchPropPathInComponent.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains a list member" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propTypeWithInternalList.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains an option member" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propTypeWithInternalOption.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains a set member" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propTypeWithInternalSet.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains a poly member" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propTypeWithInternalPoly.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a non-embeddable, non-collection, non-basic" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propPathWithNonEmbeddable.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a list" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propPathWithTerminalList.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a option" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propPathWithTerminalOption.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a set" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propPathWithTerminalSet.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a poly" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propPathWithTerminalPoly.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary list" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propPathWithInternalList.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary option" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propPathWithInternalOption.domainModel
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary set" in {
    intercept[UnsupportedPropTypeException] {
      DomainModelSpec.propPathWithInternalSet.domainModel
    }
  }

  // unlike terminal polys, intermediary polys can still be distilled down to a seq of basic components
  it should "pass when a PType contains a prop with a prop path with an intermediary poly" in {
    DomainModelSpec.propPathWithInternalPoly.domainModel
  }

  it should "throw exception when the specified prop type is incompatible with the actual type" in {
    intercept[PropTypeException] {
      DomainModelSpec.incompatiblePropType.domainModel
    }
  }

  // String <:< AnyVal, but we need requested type to be subtype of the actual type, not the other way around:
  it should "throw exception when the specified prop type is a supertype of the actual type" in {
    intercept[PropTypeException] {
      DomainModelSpec.supertypePropType.domainModel
    }
  }

  it should "throw exception when the specified prop type is a subtype of the actual type" in {
    intercept[PropTypeException] {
      DomainModelSpec.subtypePropType.domainModel
    }
  }

  it should "throw exception when the PType has multiple keys with the same key value type" in {
    intercept[DuplicateKeyException[_, _]] {
      DomainModelSpec.duplicateKey.domainModel
    }
  }

  it should "throw exception when the PType has two or more keys or indexes defined over the same properties" in {
    intercept[IndexDuplicatesKeyException] {
      DomainModelSpec.duplicateKeyOrIndex.domainModel
    }
  }

  it should "throw exception if the primary key declares an invalid partition" in {
    intercept[InvalidPartitionException[_]] {
      DomainModelSpec.invalidPartition.domainModel
    }
  }

  it should "throw exception when the PolyPType is missing from the PTypePool" in {
    intercept[DerivedHasNoPolyException] {
      DomainModelSpec.derivedPTypeHasNoPoly.domainModel
    }
  }

  it should "throw exception when the PolyCType is missing from the CTypePool" in {
    intercept[DerivedHasNoPolyException] {
      DomainModelSpec.derivedCTypeHasNoPoly.domainModel
    }
  }

  it should "throw exception when there is a duplicate CType in the CTypePool" in {
    intercept[DuplicateCTypesException] {
      DomainModelSpec.duplicateCTypes.domainModel
    }
  }

  it should "throw exception when there is a duplicate PType in the PTypePool" in {
    intercept[DuplicatePTypesException] {
      DomainModelSpec.duplicatePTypes.domainModel
    }
  }

}
