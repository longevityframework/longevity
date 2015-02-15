package emblem.traversors

import emblem._
import emblem.exceptions.CouldNotTransformException
import emblem.exceptions.CouldNotTraverseException
import emblem.traversors.Transformer._

/** holds types and zero values used by the [[Transformer transformers]] */
object Transformer {

  /** TODO scaladoc
   */
  trait CustomTransformer[A] {

    /** Visits an element of type B
     * @tparam B the type of element to generate. a subtype of A
     * @param transformer the [[Transformer]] that is delegating this call to us
     * @param input the element to visit
     */
    def apply[B <: A : TypeKey](transformer: Transformer, input: B): B

  }

  /** A [[TypeKeyMap]] for [[CustomTransformer transformer functions]] */
  type CustomTransformers = TypeKeyMap[Any, CustomTransformer]

  /** An empty map of [[CustomTransformer transformer functions]] */
  def emptyCustomTransformers: CustomTransformers = TypeKeyMap[Any, CustomTransformer]()

}

// TODO scaladoc
/** WARNING: this code is completely untested and may possibly have a design flaw */
trait Transformer {

  def transform[A : TypeKey](input: A): A = try {
    traversor.traverse[A](input)
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotTransformException(e.typeKey)
  }

  protected def shorthandPool: ShorthandPool = ShorthandPool()

  protected def emblemPool: EmblemPool = EmblemPool()

  protected def customTransformers: CustomTransformers = emptyCustomTransformers

  protected def transformBoolean(input: Boolean): Boolean = input

  protected def transformChar(input: Char): Char = input

  protected def transformDouble(input: Double): Double = input

  protected def transformFloat(input: Float): Float = input

  protected def transformInt(input: Int): Int = input

  protected def transformLong(input: Long): Long = input

  protected def transformString(input: String): String = input

  private val traversor = new Traversor {

    type TraverseInput[A] = A
    type TraverseResult[A] = A

    def traverseBoolean(input: Boolean): Boolean = transformBoolean(input)

    def traverseChar(input: Char): Char = transformChar(input)

    def traverseDouble(input: Double): Double = transformDouble(input)

    def traverseFloat(input: Float): Float = transformFloat(input)

    def traverseInt(input: Int): Int = transformInt(input)

    def traverseLong(input: Long): Long = transformLong(input)

    def traverseString(input: String): String = transformString(input)

    override protected val shorthandPool = Transformer.this.shorthandPool
    override protected val emblemPool = Transformer.this.emblemPool

    override protected val customTraversors = {
      class VisCustomTraversor[A](val customTransformer: CustomTransformer[A]) extends CustomTraversor[A] {
        def apply[B <: A : TypeKey](input: B): B =
          customTransformer.apply[B](Transformer.this, input)
      }
      val transformerToTraversor = new TypeBoundFunction[Any, CustomTransformer, CustomTraversor] {
        def apply[A](transformer: CustomTransformer[A]): CustomTraversor[A] = new VisCustomTraversor(transformer)
      }
      customTransformers.mapValues(transformerToTraversor)
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
    : A = {
      val builder = emblem.builder()
      result.foreach { case (prop, propResult) => builder.setProp(prop, propResult) }
      builder.build()
    }

    protected def stageTraverseShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      actual: Actual)
    : Abbreviated =
      shorthand.abbreviate(actual)

    protected def unstageTraverseShorthand[Actual, Abbreviated](
      shorthand: Shorthand[Actual, Abbreviated],
      abbreviated: Abbreviated)
    : Actual =
      shorthand.unabbreviate(abbreviated)

    protected def stageTraverseOptionValue[A : TypeKey](input: Option[A]): Option[A] = input

    protected def unstageTraverseOptionValue[A : TypeKey](input: Option[A], result: Option[A]): Option[A] =
      result

    protected def stageTraverseSetElements[A : TypeKey](input: Set[A]): Iterator[A] = input.iterator

    protected def unstageTraverseSetElements[A : TypeKey](input: Set[A], result: Iterator[A]): Set[A] =
      result.toSet

    protected def stageTraverseListElements[A : TypeKey](input: List[A]): Iterator[A] = input.iterator

    protected def unstageTraverseListElements[A : TypeKey](input: List[A], result: Iterator[A]): List[A] =
      result.toList

  }

}
