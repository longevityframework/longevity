package emblem.traversors

import emblem._
import emblem.exceptions.CouldNotVisitException
import emblem.exceptions.CouldNotTraverseException
import emblem.traversors.Visitor._

/** holds types and zero values used by the [[Visitor generators]] */
object Visitor {

  /** A [[TypeKeyMap]] for [[CustomVisitor generator functions]] */
  type CustomVisitors = TypeKeyMap[Any, CustomVisitor]

  /** An empty map of [[CustomVisitor generator functions]] */
  def emptyCustomVisitors: CustomVisitors = TypeKeyMap[Any, CustomVisitor]()

}

// TODO scaladoc
/** WARNING: this code is completely untested and may possibly have a design flaw */
trait Visitor {

  def visit[A : TypeKey](input: A): Unit = try {
    traversor.traverse[A](input)
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotVisitException(e.typeKey)
  }

  protected val shorthandPool: ShorthandPool = ShorthandPool()
  protected val emblemPool: EmblemPool = EmblemPool()
  protected val customVisitors: CustomVisitors = emptyCustomVisitors

  protected def visitBoolean(input: Boolean): Unit = {}

  protected def visitChar(input: Char): Unit = {}

  protected def visitDouble(input: Double): Unit = {}

  protected def visitFloat(input: Float): Unit = {}

  protected def visitInt(input: Int): Unit = {}

  protected def visitLong(input: Long): Unit = {}

  protected def visitString(input: String): Unit = {}

  private lazy val traversor = new Traversor {

    type TraverseInput[A] = A
    type TraverseEmblemInput[A <: HasEmblem] = A
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

    protected def stageTraverseEmblemProps[A <: HasEmblem](emblem: Emblem[A], input: A)
    : Iterator[TraverseEmblemPropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) = (prop, prop.get(input))
      emblem.props.map(propInput(_)).iterator
    }

    protected def unstageTraverseEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      input: A,
      result: Iterator[TraverseEmblemPropResult[A, _]])
    : Unit =
      ()

    protected def stageTraverseShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      input: Actual)
    : Abbreviated =
      shorthand.abbreviate(input)

    protected def unstageTraverseShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      abbreviatedResult: Unit)
    : Unit =
      ()

    protected def stageTraverseOptionValue[A : TypeKey](input: Option[A]): Option[A] = input

    protected def unstageTraverseOptionValue[A : TypeKey](input: Option[A], result: Option[Unit]): Unit = ()

    protected def stageTraverseSetElements[A : TypeKey](input: Set[A]): Iterator[A] = input.iterator

    protected def unstageTraverseSetElements[A : TypeKey](input: Set[A], result: Iterator[Unit]): Unit = ()

    protected def stageTraverseListElements[A : TypeKey](input: List[A]): Iterator[A] = input.iterator

    protected def unstageTraverseListElements[A : TypeKey](input: List[A], result: Iterator[Unit]): Unit = ()

  }

}
