package emblem

/** contains test data shared across specs */
package object testData {

  import emblem._

  /** for testing emblem success cases */
  object geometry {

    case class Point(x: Double, y: Double) extends HasEmblem
    lazy val pointEmblem = emblemFor[Point]
    lazy val xProp = pointEmblem.prop[Double]("x")
    lazy val yProp = pointEmblem.prop[Double]("y")

    case class Polygon(corners: Set[Point]) extends HasEmblem
    lazy val polygonEmblem = emblemFor[Polygon]
    lazy val cornersProp = polygonEmblem.prop[Set[Point]]("corners")

    case class PointWithDefaults(x: Double = 17.0, y: Double = 13.0) extends HasEmblem
    lazy val pointWithDefaultsEmblem = emblemFor[PointWithDefaults]
    lazy val xPropWithDefaults = pointWithDefaultsEmblem.prop[Double]("x")
    lazy val yPropWithDefaults = pointWithDefaultsEmblem.prop[Double]("y")

  }

  /** for testing ignored emblem cases */
  object withImplicits {

    import geometry.Point

    implicit class ImplicitBar(private val implicitBar: String) extends AnyVal {
      override def toString = implicitBar
    }
    case class FooWithImplicit(implicitBar: ImplicitBar, point: Point) extends HasEmblem
    lazy val fooWithImplicitEmblem = emblemFor[FooWithImplicit]
    lazy val implicitBarProp = fooWithImplicitEmblem.prop[ImplicitBar]("implicitBar")
    lazy val pointProp = fooWithImplicitEmblem.prop[Point]("point")

  }

  /** for shorthand happy cases */
  object shorthands {

    case class Email(email: String)
    lazy val emailShorthand = shorthandFor[Email, String]

    case class Markdown(markdown: String)
    lazy val markdownShorthand = shorthandFor[Markdown, String]

    case class Uri(uri: String)
    lazy val uriShorthand = shorthandFor[Uri, String]

  }

  /** for emblem and shorthand failure cases */
  object genFailure {

    trait NotACaseClass extends HasEmblem

    case class MultipleParamLists(i: Int)(j: Int) extends HasEmblem

    class HasInnerClass {
      case class IsInnerCaseClass(i: Int) extends HasEmblem
    }

    case class MultipleParams(i: Int, j: Int)

  }

  /** for type map happy cases */
  object computerParts {
    sealed trait ComputerPart
    case class Memory(gb: Int) extends ComputerPart
    case class CPU(mhz: Double) extends ComputerPart
    case class Display(resolution: Int) extends ComputerPart
    case class Computer(memory: Memory, cpu: CPU, display: Display)
  }

  /** for type map happy cases */
  object pets {

    trait Pet
    type PetIdentity[P <: Pet] = P

    class Cat protected (val name: String) extends Pet
    object Cat {
      private case class SullCat(override val name: String) extends Cat(name)
      def apply(name: String): Cat = SullCat(name)
    }

    class Dog protected (val name: String) extends Pet
    object Dog {
      private case class SullDog(override val name: String) extends Dog(name)
      def apply(name: String): Dog = SullDog(name)
    }

    case class Hound(override val name: String) extends Dog(name)

    class PetStore[P <: Pet]

    class PetBoxInvar[P <: Pet](var p: P)
    class PetBoxCovar[+P <: Pet](val p: P)
    class PetBoxContravar[-P <: Pet] {
      def p_=(p: P): Unit = {}
    }

  }

  /** for type map happy cases */
  object blogs {

    trait Entity
    case class User(uri: String) extends Entity
    case class Blog(uri: String) extends Entity

    trait EntityType[E <: Entity]
    object userType extends EntityType[User]
    object blogType extends EntityType[Blog]

    trait Repo[E <: Entity] {
      var saveCount = 0
      def save(entity: E): Unit = saveCount += 1
    }
    class UserRepo extends Repo[User]
    class BlogRepo extends Repo[Blog]

  }

}
