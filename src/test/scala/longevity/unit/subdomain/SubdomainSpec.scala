package longevity.unit.subdomain

import longevity.exceptions.subdomain.ptype.NoSuchPropPathException
import longevity.exceptions.subdomain.ptype.PropTypeException
import longevity.exceptions.subdomain.ptype.UnsupportedPropTypeException
import longevity.subdomain.Subdomain
import longevity.subdomain.embeddable.DerivedType
import longevity.subdomain.embeddable.ETypePool
import longevity.subdomain.embeddable.Entity
import longevity.subdomain.embeddable.EntityType
import longevity.subdomain.embeddable.PolyType
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.RootType
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** holds factory methods for sample subdomains used in [[SubdomainSpec]] */
object SubdomainSpec {

  object emptyPropPath {
    case class A(id: String) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[String]("")
      }
      object keys {
      }
      object indexes {
      }
    }
    def subdomain = Subdomain("emptyPropPath", PTypePool(A))
  }

  object noSuchPropPath {
    case class A(id: String) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[String]("noSuchPropPath")
      }
      object keys {
      }
      object indexes {
      }
    }
    def subdomain = Subdomain("noSuchPropPath", PTypePool(A))
  }

  object noSuchPropPathInComponent {
    case class A(b: B) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[String]("b.noSuchPropPath")
      }
      object keys {
      }
      object indexes {
      }
    }
    case class B(id: String) extends Entity
    object B extends EntityType[B]
    def subdomain = Subdomain("noSuchPropPathInComponent", PTypePool(A), ETypePool(B))
  }

  object propPathWithTerminalList {
    case class A(id: List[String]) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[List[String]]("id")
      }
      object keys {
      }
      object indexes {
      }
    }
    def subdomain = Subdomain("propPathWithTerminalList", PTypePool(A))
  }

  object propPathWithTerminalOption {
    case class A(id: Option[String]) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[Option[String]]("id")
      }
      object keys {
      }
      object indexes {
      }
    }
    def subdomain = Subdomain("propPathWithTerminalOption", PTypePool(A))
  }

  object propPathWithTerminalSet {
    case class A(id: Set[String]) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[Set[String]]("id")
      }
      object keys {
      }
      object indexes {
      }
    }
    def subdomain = Subdomain("propPathWithTerminalSet", PTypePool(A))
  }

  object propPathWithInternalList {
    case class A(id: List[B]) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[List[B]]("id")
      }
      object keys {
      }
      object indexes {
      }
    }
    case class B(id: String) extends Entity
    object B extends EntityType[B]
    def subdomain = Subdomain("propPathWithInternalList", PTypePool(A), ETypePool(B))
  }

  object propPathWithInternalOption {
    case class A(id: Option[B]) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[Option[B]]("id")
      }
      object keys {
      }
      object indexes {
      }
    }
    case class B(id: String) extends Entity
    object B extends EntityType[B]
    def subdomain = Subdomain("propPathWithInternalOption", PTypePool(A), ETypePool(B))
  }

  object propPathWithInternalSet {
    case class A(id: Set[B]) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[Set[B]]("id")
      }
      object keys {
      }
      object indexes {
      }
    }
    case class B(id: String) extends Entity
    object B extends EntityType[B]
    def subdomain = Subdomain("propPathWithInternalSet", PTypePool(A), ETypePool(B))
  }

  object incompatiblePropType {
    case class A(id: String) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[Double]("id")
      }
      object keys {
      }
      object indexes {
      }
    }
    def subdomain = Subdomain("incompatiblePropType", PTypePool(A))
  }

  object supertypePropType {
    case class A(id: String) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[AnyRef]("id")
      }
      object keys {
      }
      object indexes {
      }
    }
    def subdomain = Subdomain("supertypePropType", PTypePool(A))
  }

  object subtypePropType {
    case class A(b: B) extends Root
    object A extends RootType[A] {
      object props {
        val id = prop[C]("b")
      }
      object keys {
      }
      object indexes {
      }
    }

    trait B extends Entity { val id: String }
    object B extends PolyType[B]

    case class C(id: String) extends B
    object C extends DerivedType[C, B] { val polyType = B }

    def subdomain = Subdomain("subtypePropType", PTypePool(A), ETypePool(B, C))
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

  it should "pass when the specified prop type is a subtype of the actual type" in {
    SubdomainSpec.subtypePropType.subdomain
  }

}
