package emblem.emblematic.traversors.sync

import emblem.emblematic.Emblem
import emblem.emblematic.Emblematic
import emblem.emblematic.EmblemProp
import emblem.typeBound.TypeBoundFunction
import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeBound.TypeBoundFunction
import emblem.emblematic.Union
import emblem.exceptions.CouldNotVisitException
import emblem.exceptions.CouldNotTraverseException
import emblem.emblematic.traversors.sync.Visitor._
import org.joda.time.DateTime

// TODO pt-92300784 VisitorSpec

/** recursively visits a data structure by type.
 *
 * you can visit arbritrary data to your liking by implementing the protected
 * vals and defs in this interface. as yet, i haven't been able to generate the
 * scaladoc for those protected methods. sorry about that.
 *
 * WARNING: as of yet, this code is completely untested, and there is no example
 * usage for you to follow.
 */
trait Visitor {

  /** visits an element of type `A`
   * 
   * @throws emblem.exceptions.CouldNotVisitException when it encounters a type
   * it doesn't know how to visit
   */
  def visit[A : TypeKey](input: A): Unit = try {
    traversor.traverse[A](input)
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotVisitException(e.typeKey, e)
  }

  /** the emblematic types to use in the recursive visit */
  protected val emblematic: Emblematic = Emblematic.empty

  /** the custom visitors to use in the recursive visit */
  protected val customVisitors: CustomVisitorPool = CustomVisitorPool.empty

  /** visits a boolean */
  protected def visitBoolean(input: Boolean): Unit = {}

  /** visits a char */
  protected def visitChar(input: Char): Unit = {}

  /** visits a date-time */
  protected def visitDateTime(input: DateTime): Unit = {}

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

    def traverseDateTime(input: DateTime): Unit = visitDateTime(input)

    def traverseDouble(input: Double): Unit = visitDouble(input)

    def traverseFloat(input: Float): Unit = visitFloat(input)

    def traverseInt(input: Int): Unit = visitInt(input)

    def traverseLong(input: Long): Unit = visitLong(input)

    def traverseString(input: String): Unit = visitString(input)

    override protected val emblematic = Visitor.this.emblematic

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

    override protected def constituentTypeKey[A : TypeKey](union: Union[A], input: A): TypeKey[_ <: A] =
      union.typeKeyForInstance(input).get

    override protected def stageUnion[A : TypeKey, B <: A : TypeKey](union: Union[A], input: A)
    : Iterable[B] =
      Seq(input.asInstanceOf[B])

    override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: A,
      result: Iterable[Unit])
    : Unit =
      ()

    protected def stageEmblemProps[A : TypeKey](emblem: Emblem[A], input: A)
    : Iterable[PropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) = (prop, prop.get(input))
      emblem.props.map(propInput(_))
    }

    protected def unstageEmblemProps[A : TypeKey](
      emblem: Emblem[A],
      input: A,
      result: Iterable[PropResult[A, _]])
    : Unit =
      ()

    protected def stageOptionValue[A : TypeKey](input: Option[A]): Iterable[A] = input.toIterable

    protected def unstageOptionValue[A : TypeKey](input: Option[A], result: Iterable[Unit]): Unit = ()

    protected def stageSetElements[A : TypeKey](input: Set[A]): Iterable[A] = input

    protected def unstageSetElements[A : TypeKey](input: Set[A], result: Iterable[Unit]): Unit = ()

    protected def stageListElements[A : TypeKey](input: List[A]): Iterable[A] = input

    protected def unstageListElements[A : TypeKey](input: List[A], result: Iterable[Unit]): Unit = ()

  }

}

/** holds types and zero values used by the [[Visitor visitors]] */
object Visitor {

  /** a [[TypeKeyMap]] for [[CustomVisitor visitor functions]] */
  type CustomVisitorPool = TypeKeyMap[Any, CustomVisitor]

  object CustomVisitorPool {

    /** an empty map of [[CustomVisitor visitor functions]] */
    def empty: CustomVisitorPool = TypeKeyMap[Any, CustomVisitor]
  }

}
