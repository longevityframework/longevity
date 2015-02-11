package emblem.traversors

import emblem._
import emblem.traversors.Generator._
import emblem.generators.CustomGenerator
import emblem.exceptions.CouldNotGenerateException
import emblem.exceptions.CouldNotTraverseException

/** holds types and zero values used by the [[Generator generators]] */
object Generator {

  /** A [[TypeKeyMap]] for [[CustomGenerator generator functions]] */
  type CustomGenerators = TypeKeyMap[Any, CustomGenerator]

  /** An empty map of [[CustomGenerator generator functions]] */
  def emptyCustomGenerators: CustomGenerators = TypeKeyMap[Any, CustomGenerator]()

}

// TODO scaladoc
trait Generator {

  // public stuff:

  def generate[A : TypeKey]: A = try {
    traversor.traverseAny[A](())
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotGenerateException(e.typeKey)
  }

  def boolean: Boolean

  def char: Char

  def double: Double

  def float: Float

  def int: Int

  def long: Long
  
  def string: String

  // protected stuff:

  protected val shorthandPool: ShorthandPool = ShorthandPool()
  protected val emblemPool: EmblemPool = EmblemPool()
  protected val customGenerators: CustomGenerators = emptyCustomGenerators

  protected def option[A](a: => A): Option[A]

  protected def set[A](a: => A): Set[A]

  protected def list[A](a: => A): List[A]

  // private stuff:

  private val traversor = new Traversor {

    type TraverseInput[A] = Unit
    type TraverseEmblemInput[A <: HasEmblem] = HasEmblemBuilder[A]
    type TraverseResult[A] = A

    def traverseBoolean(input: Unit): Boolean = boolean

    def traverseChar(input: Unit): Char = char

    def traverseDouble(input: Unit): Double = double

    def traverseFloat(input: Unit): Float = float

    def traverseInt(input: Unit): Int = int

    def traverseLong(input: Unit): Long = long

    def traverseString(input: Unit): String = string

    override protected val shorthandPool = Generator.this.shorthandPool
    override protected val emblemPool = Generator.this.emblemPool

    override protected val customTraversors = {
      class GenCustomTraversor[A](val customGenerator: CustomGenerator[A]) extends CustomTraversor[A] {
        def apply[B <: A : TypeKey](input: Unit): B = customGenerator.apply[B](Generator.this)
      }
      val generatorToTraversor = new TypeBoundFunction[Any, CustomGenerator, CustomTraversor] {
        def apply[A](generator: CustomGenerator[A]): CustomTraversor[A] = new GenCustomTraversor(generator)
      }
      customGenerators.mapValues(generatorToTraversor)
    }

    protected def stageTraverseEmblem[A <: HasEmblem](
      emblem: Emblem[A],
      input: Unit)
    : HasEmblemBuilder[A] =
      emblem.builder()

    protected def stageTraverseEmblemProp[A <: HasEmblem, B](
      emblem: Emblem[A],
      prop: EmblemProp[A, B],
      input: HasEmblemBuilder[A])
    : Unit =
      ()

    protected def unstageTraverseEmblemProp[A <: HasEmblem, B](
      emblem: Emblem[A],
      prop: EmblemProp[A, B],
      builder: HasEmblemBuilder[A],
      propResult: B)
    : TraverseEmblemInput[A] = {
      builder.setProp(prop, propResult)
      builder
    }

    protected def unstageTraverseEmblem[A <: HasEmblem](
      emblem: Emblem[A],
      builder: HasEmblemBuilder[A])
    : A =
      builder.build()

    protected def stageTraverseShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      input: Unit)
    : Unit =
      ()

    protected def unstageTraverseShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      abbreviatedResult: Abbreviated)
    : Actual =
      shorthand.unabbreviate(abbreviatedResult)

    protected def stageTraverseOption[A : TypeKey](input: Unit): Option[Unit] = option(())

    protected def unstageTraverseOption[A : TypeKey](result: Option[A]): Option[A] = result

    protected def stageTraverseSet[A : TypeKey](input: Unit): Iterator[Unit] = list(()).iterator

    protected def unstageTraverseSet[A : TypeKey](result: Iterator[A]): Set[A] = result.toSet

    protected def stageTraverseList[A : TypeKey](input: Unit): List[Unit] = list(())

    protected def unstageTraverseList[A : TypeKey](result: List[A]): List[A] = result

  }

}
