package longevity.unit.subdomain

import longevity.exceptions.subdomain.DerivedHasNoPolyException
import longevity.exceptions.subdomain.DuplicateCTypesException
import longevity.exceptions.subdomain.DuplicateKeyException
import longevity.exceptions.subdomain.IndexDuplicatesKeyException
import longevity.exceptions.subdomain.DuplicatePTypesException
import longevity.exceptions.subdomain.InvalidPartitionException
import longevity.exceptions.subdomain.NoSuchPropPathException
import longevity.exceptions.subdomain.PropTypeException
import longevity.exceptions.subdomain.UnsupportedPropTypeException
import longevity.subdomain.DerivedCType
import longevity.subdomain.DerivedPType
import longevity.subdomain.CType
import longevity.subdomain.CTypePool
import longevity.subdomain.KeyVal
import longevity.subdomain.PType
import longevity.subdomain.PTypePool
import longevity.subdomain.PolyCType
import longevity.subdomain.PolyPType
import longevity.subdomain.Subdomain
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

  object subdomain extends Subdomain("longevity.unit.subdomain.packageScanning")
}

/** holds factory methods for sample subdomains used in [[SubdomainSpec]] */
object SubdomainSpec {

  object emptyPropPath {
    case class A(id: String)
    object A extends PType[A] {
      object props {
        val id = prop[String]("")
      }
      val keySet = emptyKeySet
    }
    def subdomain = Subdomain("emptyPropPath", PTypePool(A))
  }

  object noSuchPropPath {
    case class A(id: String)
    object A extends PType[A] {
      object props {
        val id = prop[String]("noSuchPropPath")
      }
      val keySet = emptyKeySet
    }
    def subdomain = Subdomain("noSuchPropPath", PTypePool(A))
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
    def subdomain = Subdomain("propTypeWithInternalList", PTypePool(A), CTypePool(CType[B]))
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
    def subdomain = Subdomain("propTypeWithInternalOption", PTypePool(A), CTypePool(CType[B]))
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
    def subdomain = Subdomain("propTypeWithInternalSet", PTypePool(A), CTypePool(CType[B]))
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

    def subdomain = Subdomain(
      "propTypeWithInternalPoly",
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
    def subdomain = Subdomain("noSuchPropPathInComponent", PTypePool(A), CTypePool(CType[B]))
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
    def subdomain = Subdomain("propPathWithNonEmbeddable", PTypePool(A))
  }

  object propPathWithTerminalList {
    case class A(id: List[String])
    object A extends PType[A] {
      object props {
        val id = prop[List[String]]("id")
      }
      val keySet = emptyKeySet
    }
    def subdomain = Subdomain("propPathWithTerminalList", PTypePool(A))
  }

  object propPathWithTerminalOption {
    case class A(id: Option[String])
    object A extends PType[A] {
      object props {
        val id = prop[Option[String]]("id")
      }
      val keySet = emptyKeySet
    }
    def subdomain = Subdomain("propPathWithTerminalOption", PTypePool(A))
  }

  object propPathWithTerminalSet {
    case class A(id: Set[String])
    object A extends PType[A] {
      object props {
        val id = prop[Set[String]]("id")
      }
      val keySet = emptyKeySet
    }
    def subdomain = Subdomain("propPathWithTerminalSet", PTypePool(A))
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

    def subdomain = Subdomain(
      "propPathWithTerminalPoly",
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
    def subdomain = Subdomain("propPathWithInternalList", PTypePool(A), CTypePool(CType[B]))
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
    def subdomain = Subdomain("propPathWithInternalOption", PTypePool(A), CTypePool(CType[B]))
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
    def subdomain = Subdomain("propPathWithInternalSet", PTypePool(A), CTypePool(CType[B]))
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

    def subdomain = Subdomain(
      "propPathWithInternalPoly",
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
    def subdomain = Subdomain("incompatiblePropType", PTypePool(A))
  }

  object supertypePropType {
    case class A(id: String)
    object A extends PType[A] {
      object props {
        val id = prop[AnyRef]("id")
      }
      val keySet = emptyKeySet
    }
    def subdomain = Subdomain("supertypePropType", PTypePool(A))
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

    def subdomain = Subdomain("subtypePropType", PTypePool(A))
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

    def subdomain = Subdomain("duplicateKey", PTypePool(A))

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

    def subdomain = Subdomain("duplicateKeyOrIndex", PTypePool(A))

  }

  object invalidPartition {

    case class AId(id1: String, id2: String) extends KeyVal[A]

    case class A(id: AId)
    object A extends PType[A] {
      object props {
        val id = prop[AId]("id")
        val id2 = prop[String]("id.id2")
      }
      val keySet = Set(partitionKey(props.id, partition(props.id2)))
    }

    def subdomain = Subdomain("invalidPartition", PTypePool(A))

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

    def subdomain = Subdomain("derivedPTypeHasNoPoly", PTypePool(Derived))
  }

  object derivedCTypeHasNoPoly {
    trait Poly { val id: String }
    case class Derived(id: String) extends Poly
    def subdomain = Subdomain("derivedCTypeHasNoPoly", PTypePool(), CTypePool(DerivedCType[Derived, Poly]))
  }

  object duplicateCTypes {
    case class A(id: String)
    def subdomain = Subdomain("duplicateCTypes", PTypePool(), CTypePool(CType[A], CType[A]))
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
    def subdomain = Subdomain("duplicatePTypes", PTypePool(A, B))
  }

}

/** unit tests for the proper [[Subdomain]] construction */
class SubdomainSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "Subdomain package scanning"

  it should "collect objects containing PTypes and CTypes" in {
    println(packageScanning.subdomain)
    packageScanning.subdomain.pTypePool.size should equal (1)
    packageScanning.subdomain.cTypePool.size should equal (1)
  }

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

  it should "throw exception when a PType contains a prop with a type that contains a list member" in {
    intercept[UnsupportedPropTypeException] {
      SubdomainSpec.propTypeWithInternalList.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains an option member" in {
    intercept[UnsupportedPropTypeException] {
      SubdomainSpec.propTypeWithInternalOption.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains a set member" in {
    intercept[UnsupportedPropTypeException] {
      SubdomainSpec.propTypeWithInternalSet.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a type that contains a poly member" in {
    intercept[UnsupportedPropTypeException] {
      SubdomainSpec.propTypeWithInternalPoly.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a non-embeddable, non-collection, non-basic" in {
    intercept[UnsupportedPropTypeException] {
      SubdomainSpec.propPathWithNonEmbeddable.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a list" in {
    intercept[UnsupportedPropTypeException] {
      SubdomainSpec.propPathWithTerminalList.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a option" in {
    intercept[UnsupportedPropTypeException] {
      SubdomainSpec.propPathWithTerminalOption.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a set" in {
    intercept[UnsupportedPropTypeException] {
      SubdomainSpec.propPathWithTerminalSet.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path that terminates with a poly" in {
    intercept[UnsupportedPropTypeException] {
      SubdomainSpec.propPathWithTerminalPoly.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary list" in {
    intercept[UnsupportedPropTypeException] {
      SubdomainSpec.propPathWithInternalList.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary option" in {
    intercept[UnsupportedPropTypeException] {
      SubdomainSpec.propPathWithInternalOption.subdomain
    }
  }

  it should "throw exception when a PType contains a prop with a prop path with an intermediary set" in {
    intercept[UnsupportedPropTypeException] {
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

  it should "throw exception when the PType has two or more keys or indexes defined over the same properties" in {
    intercept[IndexDuplicatesKeyException] {
      SubdomainSpec.duplicateKeyOrIndex.subdomain
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

  it should "throw exception when the PolyCType is missing from the CTypePool" in {
    intercept[DerivedHasNoPolyException] {
      SubdomainSpec.derivedCTypeHasNoPoly.subdomain
    }
  }

  it should "throw exception when there is a duplicate CType in the CTypePool" in {
    intercept[DuplicateCTypesException] {
      SubdomainSpec.duplicateCTypes.subdomain
    }
  }

  it should "throw exception when there is a duplicate PType in the PTypePool" in {
    intercept[DuplicatePTypesException] {
      SubdomainSpec.duplicatePTypes.subdomain
    }
  }

}
