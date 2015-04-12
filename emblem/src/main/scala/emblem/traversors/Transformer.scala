package emblem.traversors

import emblem.imports._
import emblem.TypeBoundFunction
import emblem.exceptions.CouldNotTransformException
import emblem.exceptions.CouldNotTraverseException
import emblem.exceptions.ExtractorInverseException
import emblem.traversors.Transformer._

/** recursively tranforms a data structure by type. the input and the output of the transformation
 * have the same type.
 *
 * you can transform arbritrary data to your liking by implementing the protected vals and defs in this
 * interface. as of yet, i haven't been able to generate the scaladoc for those protected methods.
 * sorry about that.
 *
 * the only usage example as of now, `longevity.testUtil.PersistedToUnpersistedTransformer`, lives outside of
 * emblem project, in sibling project longevity. it might give you some ideas in how to use, but then so will
 * other traversors in this directory.
 */
trait Transformer {

  /** transforms an element of type `A`
   * @throws emblem.exceptions.CouldNotTransformException when it encounters a type it doesn't know how to
   * transform
   */
  def transform[A : TypeKey](input: A): A = try {
    traversor.traverse[A](input)
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotTransformException(e.typeKey, e)
  }

  /** the emblems to use in the recursive transformation */
  protected val emblemPool: EmblemPool = EmblemPool.empty

  /** the extractors to use in the recursive transformation */
  protected val extractorPool: ExtractorPool = ExtractorPool.empty

  /** the custom transformers to use in the recursive transformation */
  protected val customTransformers: CustomTransformerPool = CustomTransformerPool.empty

  /** transforms a boolean */
  protected def transformBoolean(input: Boolean): Boolean = input

  /** transforms a char */
  protected def transformChar(input: Char): Char = input

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

    def traverseDouble(input: Double): Double = transformDouble(input)

    def traverseFloat(input: Float): Float = transformFloat(input)

    def traverseInt(input: Int): Int = transformInt(input)

    def traverseLong(input: Long): Long = transformLong(input)

    def traverseString(input: String): String = transformString(input)

    override protected val extractorPool = Transformer.this.extractorPool
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

    protected def stageEmblemProps[A <: HasEmblem](emblem: Emblem[A], input: A)
    : Iterator[PropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) = (prop, prop.get(input))
      emblem.props.map(propInput(_)).iterator
    }

    protected def unstageEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      input: A,
      result: Iterator[PropResult[A, _]])
    : A = {
      val builder = emblem.builder()
      result.foreach { case (prop, propResult) => builder.setProp(prop, propResult) }
      builder.build()
    }

    protected def stageExtractor[Domain : TypeKey, Range](
      extractor: Extractor[Domain, Range],
      domain: Domain)
    : Range =
      extractor.apply(domain)

    protected def unstageExtractor[Domain : TypeKey, Range](
      extractor: Extractor[Domain, Range],
      range: Range)
    : Domain =
      try {
        extractor.inverse(range)
      } catch {
        case e: Exception => throw new ExtractorInverseException(range, typeKey[Domain], e)
      }

    protected def stageOptionValue[A : TypeKey](input: Option[A]): Option[A] = input

    protected def unstageOptionValue[A : TypeKey](input: Option[A], result: Option[A]): Option[A] = result

    protected def stageSetElements[A : TypeKey](input: Set[A]): Iterator[A] = input.iterator

    protected def unstageSetElements[A : TypeKey](input: Set[A], result: Iterator[A]): Set[A] = result.toSet

    protected def stageListElements[A : TypeKey](input: List[A]): Iterator[A] = input.iterator

    protected def unstageListElements[A : TypeKey](input: List[A], result: Iterator[A]): List[A] = result.toList

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
