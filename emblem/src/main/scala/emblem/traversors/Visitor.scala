package emblem.traversors

import emblem._
import emblem.exceptions.CouldNotVisitException
import emblem.exceptions.CouldNotTraverseException
import emblem.traversors.Visitor._

// TODO: scaladoc for: Traversor

/** holds types and zero values used by the [[Visitor generators]] */
object Visitor {

  /** A [[TypeKeyMap]] for [[CustomVisitor generator functions]] */
  type CustomVisitors = TypeKeyMap[Any, CustomVisitor]

  /** An empty map of [[CustomVisitor generator functions]] */
  def emptyCustomVisitors: CustomVisitors = TypeKeyMap[Any, CustomVisitor]()

}

/** visits data as requested by type.
 *
 * you can visit arbritrary data to your liking by implementing the protected vals and defs in this
 * interface. as yet, i haven't been able to generate the scaladoc for those protected methods.
 * sorry about that.
 *
 * WARNING: as of yet, this code is completely untested, and there is no example usage for you to follow.
 */
trait Visitor {

  /** visits an element of type A
   * @throws emblem.exceptions.CouldNotVisitException when it encounters a type it doesn't know how to
   * visit
   */
  def visit[A : TypeKey](input: A): Unit = try {
    traversor.traverse[A](input)
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotVisitException(e.typeKey, e)
  }

  /** the shorthands to use in the recursive visit */
  protected val shorthandPool: ShorthandPool = ShorthandPool()

  /** the emblems to use in the recursive visit */
  protected val emblemPool: EmblemPool = EmblemPool()

  /** the custom visitors to use in the recursive visit */
  protected val customVisitors: CustomVisitors = emptyCustomVisitors

  /** visits a boolean */
  protected def visitBoolean(input: Boolean): Unit = {}

  /** visits a chat */
  protected def visitChar(input: Char): Unit = {}

  /** visits a double */
  protected def visitDouble(input: Double): Unit = {}

  /** visits a float */
  protected def visitFloat(input: Float): Unit = {}

  /** visits an int */
  protected def visitInt(input: Int): Unit = {}

  /** visits a long */
  protected def visitLong(input: Long): Unit = {}

  /** visits a string */
  protected def visitString(input: String): Unit = {}

  private lazy val traversor = new Traversor {

    type TraverseInput[A] = A
    type TraverseResult[A] = Unit

    def traverseBoolean(input: Boolean): Unit = visitBoolean(input)

    def traverseChar(input: Char): Unit = visitChar(input)

    def traverseDouble(input: Double): Unit = visitDouble(input)

    def traverseFloat(input: Float): Unit = visitFloat(input)

    def traverseInt(input: Int): Unit = visitInt(input)

    def traverseLong(input: Long): Unit = visitLong(input)

    def traverseString(input: String): Unit = visitString(input)

    override protected val shorthandPool = Visitor.this.shorthandPool
    override protected val emblemPool = Visitor.this.emblemPool

    override protected val customTraversors = {
      class VisCustomTraversor[A](val customVisitor: CustomVisitor[A]) extends CustomTraversor[A] {
        def apply[B <: A : TypeKey](input: B): Unit =
          customVisitor.apply[B](Visitor.this, input)
      }
      val visitorToTraversor = new TypeBoundFunction[Any, CustomVisitor, CustomTraversor] {
        def apply[A](visitor: CustomVisitor[A]): CustomTraversor[A] = new VisCustomTraversor(visitor)
      }
      customVisitors.mapValues(visitorToTraversor)
    }

    protected def stageEmblemProps[A <: HasEmblem](emblem: Emblem[A], input: A)
    : Iterator[TraverseEmblemPropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) = (prop, prop.get(input))
      emblem.props.map(propInput(_)).iterator
    }

    protected def unstageEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      input: A,
      result: Iterator[TraverseEmblemPropResult[A, _]])
    : Unit =
      ()

    protected def stageShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      input: Actual)
    : Abbreviated =
      shorthand.abbreviate(input)

    protected def unstageShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      abbreviatedResult: Unit)
    : Unit =
      ()

    protected def stageOptionValue[A : TypeKey](input: Option[A]): Option[A] = input

    protected def unstageOptionValue[A : TypeKey](input: Option[A], result: Option[Unit]): Unit = ()

    protected def stageSetElements[A : TypeKey](input: Set[A]): Iterator[A] = input.iterator

    protected def unstageSetElements[A : TypeKey](input: Set[A], result: Iterator[Unit]): Unit = ()

    protected def stageListElements[A : TypeKey](input: List[A]): Iterator[A] = input.iterator

    protected def unstageListElements[A : TypeKey](input: List[A], result: Iterator[Unit]): Unit = ()

  }

}
