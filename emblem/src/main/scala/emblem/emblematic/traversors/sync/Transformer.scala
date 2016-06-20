package emblem.emblematic.traversors.sync

import emblem.emblematic.Emblem
import emblem.emblematic.Emblematic
import emblem.emblematic.EmblemProp
import emblem.emblematic.Extractor
import emblem.typeBound.TypeBoundFunction
import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.emblematic.Union
import emblem.exceptions.CouldNotTransformException
import emblem.exceptions.CouldNotTraverseException
import emblem.exceptions.ExtractorInverseException
import emblem.emblematic.traversors.sync.Transformer.CustomTransformer
import emblem.emblematic.traversors.sync.Transformer.CustomTransformerPool
import emblem.typeKey
import org.joda.time.DateTime

/** synchronously tranforms a recursive data structure. the input and the
 * output of the transformation have the same type.
 *
 * you can transform arbritrary data to your liking by implementing the
 * protected vals and defs in this interface.
 *
 * the only usage example as of now,
 * `longevity.testUtil.PersistedToUnpersistedTransformer`, lives outside of
 * emblem project, in sibling project longevity. it might give you some ideas
 * in how to use, but then so will other traversors in this directory.
 */
trait Transformer {

  /** transforms an element of type `A`
   * @throws emblem.exceptions.CouldNotTransformException when it encounters
   * a type it doesn't know how to transform
   */
  def transform[A : TypeKey](input: A): A = try {
    traversor.traverse[A](input)
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotTransformException(e.typeKey, e)
  }

  /** the emblematic types to use in the recursive transformation */
  protected val emblematic: Emblematic = Emblematic.empty

  /** the custom transformers to use in the recursive transformation */
  protected val customTransformers: CustomTransformerPool = CustomTransformerPool.empty

  /** transforms a boolean */
  protected def transformBoolean(input: Boolean): Boolean = input

  /** transforms a char */
  protected def transformChar(input: Char): Char = input

  /** transforms a date-time */
  protected def transformDateTime(input: DateTime): DateTime = input

  /** transforms a double */
  protected def transformDouble(input: Double): Double = input

  /** transforms a float */
  protected def transformFloat(input: Float): Float = input

  /** transforms an int */
  protected def transformInt(input: Int): Int = input

  /** transforms a long */
  protected def transformLong(input: Long): Long = input

  /** transforms a string */
  protected def transformString(input: String): String = input

  private lazy val traversor = new Traversor {

    type TraverseInput[A] = A
    type TraverseResult[A] = A

    def traverseBoolean(input: Boolean): Boolean = transformBoolean(input)

    def traverseChar(input: Char): Char = transformChar(input)

    def traverseDateTime(input: DateTime): DateTime = transformDateTime(input)

    def traverseDouble(input: Double): Double = transformDouble(input)

    def traverseFloat(input: Float): Float = transformFloat(input)

    def traverseInt(input: Int): Int = transformInt(input)

    def traverseLong(input: Long): Long = transformLong(input)

    def traverseString(input: String): String = transformString(input)

    override protected val emblematic = Transformer.this.emblematic

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

    override protected def constituentTypeKey[A : TypeKey](union: Union[A], input: A): TypeKey[_ <: A] =
      union.typeKeyForInstance(input).get

    override protected def stageUnion[A : TypeKey, B <: A : TypeKey](union: Union[A], input: A): Iterable[B] =
      Some(input.asInstanceOf[B])

    override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: A,
      result: Iterable[B])
    : A =
      result.head

    override protected def stageEmblemProps[A : TypeKey](emblem: Emblem[A], input: A)
    : Iterable[PropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) = (prop, prop.get(input))
      emblem.props.map(propInput(_))
    }

    override protected def unstageEmblemProps[A : TypeKey](
      emblem: Emblem[A],
      input: A,
      result: Iterable[PropResult[A, _]])
    : A = {
      val builder = emblem.builder()
      result.foreach { case (prop, propResult) => builder.setProp(prop, propResult) }
      builder.build()
    }

    override protected def stageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      domain: Domain)
    : Range =
      extractor.apply(domain)

    override protected def unstageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      range: Range)
    : Domain =
      try {
        extractor.inverse(range)
      } catch {
        case e: Exception => throw new ExtractorInverseException(range, typeKey[Domain], e)
      }

    override protected def stageOptionValue[A : TypeKey](input: Option[A]): Iterable[A] =
      input.toIterable

    override protected def unstageOptionValue[A : TypeKey](input: Option[A], result: Iterable[A]): Option[A] =
      result.headOption

    override protected def stageSetElements[A : TypeKey](input: Set[A]): Iterable[A] =
      input

    override protected def unstageSetElements[A : TypeKey](input: Set[A], result: Iterable[A]): Set[A] =
      result.toSet

    override protected def stageListElements[A : TypeKey](input: List[A]): Iterable[A] =
      input

    override protected def unstageListElements[A : TypeKey](input: List[A], result: Iterable[A]): List[A] =
      result.toList

  }

}

/** holds types and zero values used by the [[Transformer transformers]], and supplies the API for custom
 * tranformers
 */
object Transformer {

  /** a custom transformer of things of type A */
  trait CustomTransformer[A] {

    /** transforms an element of type `B`
     * @tparam B the type of element to transform. a subtype of `A`
     * @param transformer the [[Transformer]] that is delegating this call to us
     * @param input the element to transform
     */
    def apply[B <: A : TypeKey](transformer: Transformer, input: B): B

  }

  /** a [[TypeKeyMap]] for [[CustomTransformer transformer functions]] */
  type CustomTransformerPool = TypeKeyMap[Any, CustomTransformer]

  object CustomTransformerPool {

    /** an empty map of [[CustomTransformer transformer functions]] */
    def empty: CustomTransformerPool = TypeKeyMap[Any, CustomTransformer]()
  }

}
