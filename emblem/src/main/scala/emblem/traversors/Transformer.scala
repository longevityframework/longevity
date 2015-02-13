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
    type TraverseEmblemInput[A <: HasEmblem] = (A, HasEmblemBuilder[A])
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

    protected def stageTraverseEmblem[A <: HasEmblem](
      emblem: Emblem[A],
      input: A)
    : (A, HasEmblemBuilder[A]) =
      (input, emblem.builder())

    protected def stageTraverseEmblemProp[A <: HasEmblem, B](
      emblem: Emblem[A],
      prop: EmblemProp[A, B],
      input: (A, HasEmblemBuilder[A]))
    : B =
      prop.get(input._1)

    protected def unstageTraverseEmblemProp[A <: HasEmblem, B](
      emblem: Emblem[A],
      prop: EmblemProp[A, B],
      input: (A, HasEmblemBuilder[A]),
      propResult: B)
    : (A, HasEmblemBuilder[A]) = {
      input._2.setProp(prop, propResult)
      input
    }

    protected def unstageTraverseEmblem[A <: HasEmblem](
      emblem: Emblem[A],
      input: (A, HasEmblemBuilder[A]))
    : A =
      input._2.build()

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

    protected def unstageTraverseOptionValue[A : TypeKey](result: Option[A]): Option[A] = result

    protected def stageTraverseSetElements[A : TypeKey](input: Set[A]): Iterator[A] = input.iterator

    protected def unstageTraverseSetElements[A : TypeKey](result: Iterator[A]): Set[A] = result.toSet

    protected def stageTraverseListElements[A : TypeKey](input: List[A]): List[A] = input

    protected def unstageTraverseListElements[A : TypeKey](result: List[A]): List[A] = result

  }

}
