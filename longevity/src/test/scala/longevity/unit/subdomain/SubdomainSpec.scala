package longevity.unit.subdomain

import longevity.exceptions.subdomain.DerivedHasNoPolyException
import longevity.exceptions.subdomain.DuplicateETypesException
import longevity.exceptions.subdomain.DuplicateKeyException
import longevity.exceptions.subdomain.DuplicatePTypesException
import longevity.exceptions.subdomain.InvalidPartitionException
import longevity.exceptions.subdomain.NoSuchPropPathException
import longevity.exceptions.subdomain.PropTypeException
import longevity.exceptions.subdomain.UnsupportedPropTypeException
import longevity.subdomain.DerivedEType
import longevity.subdomain.DerivedPType
import longevity.subdomain.EType
import longevity.subdomain.EType
import longevity.subdomain.ETypePool
import longevity.subdomain.Embeddable
import longevity.subdomain.Embeddable
import longevity.subdomain.KeyVal
import longevity.subdomain.PType
import longevity.subdomain.PType
import longevity.subdomain.PTypePool
import longevity.subdomain.Persistent
import longevity.subdomain.Persistent
import longevity.subdomain.PolyEType
import longevity.subdomain.PolyPType
import longevity.subdomain.Subdomain
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** holds factory methods for sample subdomains used in [[SubdomainSpec]] */
object SubdomainSpec {

  object emptyPropPath {
    case class A(id: String) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[String]("")
      }
      object keys {
      }
    }
    def subdomain = Subdomain("emptyPropPath", PTypePool(A))
  }

  object noSuchPropPath {
    case class A(id: String) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[String]("noSuchPropPath")
      }
      object keys {
      }
    }
    def subdomain = Subdomain("noSuchPropPath", PTypePool(A))
  }

  object noSuchPropPathInComponent {
    case class A(b: B) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[String]("b.noSuchPropPath")
      }
      object keys {
      }
    }
    case class B(id: String) extends Embeddable
    def subdomain = Subdomain("noSuchPropPathInComponent", PTypePool(A), ETypePool(EType[B]))
  }

  object propPathWithNonEmbeddable {
    import java.util.UUID
    case class A(id: UUID) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[UUID]("id")
      }
      object keys {
      }
    }
    def subdomain = Subdomain("propPathWithNonEmbeddable", PTypePool(A))
  }

  object propPathWithTerminalList {
    case class A(id: List[String]) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[List[String]]("id")
      }
      object keys {
      }
    }
    def subdomain = Subdomain("propPathWithTerminalList", PTypePool(A))
  }

  object propPathWithTerminalOption {
    case class A(id: Option[String]) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[Option[String]]("id")
      }
      object keys {
      }
    }
    def subdomain = Subdomain("propPathWithTerminalOption", PTypePool(A))
  }

  object propPathWithTerminalSet {
    case class A(id: Set[String]) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[Set[String]]("id")
      }
      object keys {
      }
    }
    def subdomain = Subdomain("propPathWithTerminalSet", PTypePool(A))
  }

  object propPathWithTerminalPoly {
    case class A(b: B) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[B]("b")
      }
      object keys {
      }
    }

    trait B extends Embeddable { val id: String }

    case class C(id: String) extends B

    def subdomain = Subdomain(
      "propPathWithTerminalPoly",
      PTypePool(A),
      ETypePool(PolyEType[B], DerivedEType[C, B]))
  }

  object propPathWithInternalList {
    case class A(id: List[B]) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[String]("id.id")
      }
      object keys {
      }
    }
    case class B(id: String) extends Embeddable
    def subdomain = Subdomain("propPathWithInternalList", PTypePool(A), ETypePool(EType[B]))
  }

  object propPathWithInternalOption {
    case class A(id: Option[B]) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[String]("id.id")
      }
      object keys {
      }
    }
    case class B(id: String) extends Embeddable
    def subdomain = Subdomain("propPathWithInternalOption", PTypePool(A), ETypePool(EType[B]))
  }

  object propPathWithInternalSet {
    case class A(id: Set[B]) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[String]("id.id")
      }
      object keys {
      }
    }
    case class B(id: String) extends Embeddable
    def subdomain = Subdomain("propPathWithInternalSet", PTypePool(A), ETypePool(EType[B]))
  }

  object propPathWithInternalPoly {
    case class A(b: B) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[String]("b.id")
      }
      object keys {
      }
    }

    trait B extends Embeddable { val id: String }
    case class C(id: String) extends B

    def subdomain = Subdomain(
      "propPathWithInternalPoly",
      PTypePool(A),
      ETypePool(PolyEType[B], DerivedEType[C, B]))
  }

  object incompatiblePropType {
    case class A(id: String) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[Double]("id")
      }
      object keys {
      }
    }
    def subdomain = Subdomain("incompatiblePropType", PTypePool(A))
  }

  object supertypePropType {
    case class A(id: String) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[AnyRef]("id")
      }
      object keys {
      }
    }
    def subdomain = Subdomain("supertypePropType", PTypePool(A))
  }

  object subtypePropType {

    case class AId(id: String) extends KeyVal[A, AId]

    case class A(id: AId) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[AId]("id")
        val id2 = prop[KeyVal[A, _ <: KeyVal[A, _]]]("id") // this is the problematic prop
      }
      object keys {
        val id = key(props.id)
      }
    }

    def subdomain = Subdomain("subtypePropType", PTypePool(A))
  }

  object duplicateKey {

    case class AId(id: String) extends KeyVal[A, AId]

    case class A(id1: AId, id2: AId) extends Persistent
    object A extends PType[A] {
      object props {
        val id1 = prop[AId]("id1")
        val id2 = prop[AId]("id2")
      }
      object keys {
        val id1 = key(props.id1)
        val id2 = key(props.id2)
      }
    }

    def subdomain = Subdomain("subtypePropType", PTypePool(A))

  }

  object invalidPartition {

    case class AId(id1: String, id2: String) extends KeyVal[A, AId]

    case class A(id: AId) extends Persistent
    object A extends PType[A] {
      object props {
        val id = prop[AId]("id")
        val id2 = prop[String]("id.id2")
      }
      object keys {
        val id = partitionKey(props.id, partition(props.id2))
      }
    }

    def subdomain = Subdomain("invalidPartition", PTypePool(A))

  }

  object derivedPTypeHasNoPoly {

    trait Poly extends Persistent { val id: String }
    object Poly extends PolyPType[Poly] {
      object props {
      }
      object keys {
      }
    }

    case class Derived(id: String) extends Poly
    object Derived extends DerivedPType[Derived, Poly] {
      object props {
      }
      object keys {
      }
    }

    def subdomain = Subdomain("derivedPTypeHasNoPoly", PTypePool(Derived))
  }

  object derivedETypeHasNoPoly {
    trait Poly extends Embeddable { val id: String }
    case class Derived(id: String) extends Poly
    def subdomain = Subdomain("derivedETypeHasNoPoly", PTypePool(), ETypePool(DerivedEType[Derived, Poly]))
  }

  object duplicateETypes {
    case class A(id: String) extends Embeddable
    def subdomain = Subdomain("duplicateETypes", PTypePool(), ETypePool(EType[A], EType[A]))
  }

  object duplicatePTypes {
    case class A(id: String) extends Persistent
    object A extends PType[A] {
      object props {
      }
      object keys {
      }
    }
    object B extends PType[A] {
      object props {
      }
      object keys {
      }
    }
    def subdomain = Subdomain("duplicatePTypes", PTypePool(A, B))
  }

}

/** unit tests for the proper [[Subdomain]] construction */
class SubdomainSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "Subdomain creation"

  it should "throw exception when a PType contains a prop with an empty prop path" in {
    intercept[NoSuchPropPathException] {
      SubdomainSpec.emptyPropPath.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path not found in the persistent type" in {
    intercept[NoSuchPropPathException] {
      SubdomainSpec.noSuchPropPath.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path not found in the component" in {
    intercept[NoSuchPropPathException] {
      SubdomainSpec.noSuchPropPathInComponent.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a non-embeddable, non-collection, non-basic" in {
    intercept[UnsupportedPropTypeException[_, _]] {
      SubdomainSpec.propPathWithNonEmbeddable.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a list" in {
    intercept[UnsupportedPropTypeException[_, _]] {
      SubdomainSpec.propPathWithTerminalList.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a option" in {
    intercept[UnsupportedPropTypeException[_, _]] {
      SubdomainSpec.propPathWithTerminalOption.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a set" in {
    intercept[UnsupportedPropTypeException[_, _]] {
      SubdomainSpec.propPathWithTerminalSet.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a poly" in {
    intercept[UnsupportedPropTypeException[_, _]] {
      SubdomainSpec.propPathWithTerminalPoly.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary list" in {
    intercept[UnsupportedPropTypeException[_, _]] {
      SubdomainSpec.propPathWithInternalList.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary option" in {
    intercept[UnsupportedPropTypeException[_, _]] {
      SubdomainSpec.propPathWithInternalOption.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary set" in {
    intercept[UnsupportedPropTypeException[_, _]] {
      SubdomainSpec.propPathWithInternalSet.subdomain
    }
  }

  // unlike terminal polys, intermediary polys can still be distilled down to a seq of basic components
  it should "pass when a PType contains a prop with a prop path with an intermediary poly" in {
    SubdomainSpec.propPathWithInternalPoly.subdomain
  }

  it should "throw exception when the specified prop type is incompatible with the actual type" in {
    intercept[PropTypeException] {
      SubdomainSpec.incompatiblePropType.subdomain
    }
  }

  // String <:< AnyVal, but we need requested type to be subtype of the actual type, not the other way around:
  it should "throw exception when the specified prop type is a supertype of the actual type" in {
    intercept[PropTypeException] {
      SubdomainSpec.supertypePropType.subdomain
    }
  }

  it should "throw exception when the specified prop type is a subtype of the actual type" in {
    intercept[PropTypeException] {
      SubdomainSpec.subtypePropType.subdomain
    }
  }

  it should "throw exception when the PType has multiple keys with the same key value type" in {
    intercept[DuplicateKeyException[_, _]] {
      SubdomainSpec.duplicateKey.subdomain
    }
  }

  it should "throw exception if the partition key declares an invalid partition" in {
    intercept[InvalidPartitionException[_]] {
      SubdomainSpec.invalidPartition.subdomain
    }
  }

  it should "throw exception when the PolyPType is missing from the PTypePool" in {
    intercept[DerivedHasNoPolyException] {
      SubdomainSpec.derivedPTypeHasNoPoly.subdomain
    }
  }

  it should "throw exception when the PolyEType is missing from the ETypePool" in {
    intercept[DerivedHasNoPolyException] {
      SubdomainSpec.derivedETypeHasNoPoly.subdomain
    }
  }

  it should "throw exception when there is a duplicate EType in the ETypePool" in {
    intercept[DuplicateETypesException] {
      SubdomainSpec.duplicateETypes.subdomain
    }
  }

  it should "throw exception when there is a duplicate PType in the PTypePool" in {
    intercept[DuplicatePTypesException] {
      SubdomainSpec.duplicatePTypes.subdomain
    }
  }

}
