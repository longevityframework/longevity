package emblem.traversors

import emblem._
import emblem.exceptions.CouldNotGenerateException
import emblem.exceptions.CouldNotTraverseException
import emblem.traversors.Generator._

/** holds types and zero values used by the [[Generator generators]] */
object Generator {

  /** A [[TypeKeyMap]] for [[CustomGenerator generator functions]] */
  type CustomGenerators = TypeKeyMap[Any, CustomGenerator]

  /** An empty map of [[CustomGenerator generator functions]] */
  def emptyCustomGenerators: CustomGenerators = TypeKeyMap[Any, CustomGenerator]()

}

/** generates data as requested by type. */
trait Generator {

  // public stuff:

  /** generates data for the specified type A
   * @tparam A the type of data to generate
   * @return the generated data
   * @throws emblem.exceptions.CouldNotGenerateException when we encounter a type in the recursive traversal
   * that we don't know how to generate for
   */
  def generate[A : TypeKey]: A = try {
    traversor.traverse[A](())
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotGenerateException(e.typeKey)
  }

  // protected stuff:

  protected val shorthandPool: ShorthandPool = ShorthandPool()
  protected val emblemPool: EmblemPool = EmblemPool()
  protected val customGenerators: CustomGenerators = emptyCustomGenerators

  protected def option[A](a: => A): Option[A]

  protected def set[A](a: => A): Set[A]

  protected def list[A](a: => A): List[A]

  protected def boolean: Boolean

  protected def char: Char

  protected def double: Double

  protected def float: Float

  protected def int: Int

  protected def long: Long
  
  protected def string: String

  // private stuff:

  private val traversor = new Traversor {

    type TraverseInput[A] = Unit
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

    protected def stageEmblemProps[A <: HasEmblem](emblem: Emblem[A], input: Unit)
    : Iterator[TraverseEmblemPropInput[A, _]] =
      emblem.props.map((_, ())).iterator

    protected def unstageEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      input: Unit,
      result: Iterator[TraverseEmblemPropResult[A, _]])
    : A = {
      val builder = emblem.builder()
      result.foreach { case (prop, propResult) => builder.setProp(prop, propResult) }
      builder.build()
    }

    protected def stageShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      input: Unit)
    : Unit =
      ()

    protected def unstageShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      abbreviatedResult: Abbreviated)
    : Actual =
      shorthand.unabbreviate(abbreviatedResult)

    protected def stageOptionValue[A : TypeKey](input: Unit): Option[Unit] = option(())

    protected def unstageOptionValue[A : TypeKey](input: Unit, result: Option[A]): Option[A] = result

    protected def stageSetElements[A : TypeKey](input: Unit): Iterator[Unit] = list(()).iterator

    protected def unstageSetElements[A : TypeKey](input: Unit, result: Iterator[A]): Set[A] =
      result.toSet

    protected def stageListElements[A : TypeKey](input: Unit): Iterator[Unit] = list(()).iterator

    protected def unstageListElements[A : TypeKey](input: Unit, result: Iterator[A]): List[A] =
      result.toList

  }

}
