package emblem.traversors.async

import emblem.Emblem
import emblem.Emblematic
import emblem.EmblemPool
import emblem.EmblemProp
import emblem.Extractor
import emblem.ExtractorPool
import emblem.HasEmblem
import emblem.TypeBoundFunction
import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.exceptions.CouldNotTransformException
import emblem.exceptions.CouldNotTraverseException
import emblem.exceptions.ExtractorInverseException
import emblem.traversors.async.Transformer._
import emblem.typeKey
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.util.Failure
import scala.util.Success

/** asynchronously tranforms a recursive data structure. the input and the
 * output of the transformation have the same type.
 *
 * you can transform arbritrary data to your liking by implementing the
 * protected vals and defs in this interface.
 *
 * the only usage example as of now,
 * `longevity.persistence.UnpersistedToPersistedTransformer`, lives outside of
 * emblem project, in sibling project longevity. it might give you some ideas in
 * how to use.
 */
trait Transformer {

  /** transforms an element of type `A`
   * 
   * @throws emblem.exceptions.CouldNotTransformException when it encounters a
   * type it doesn't know how to transform
   */
  def transform[A : TypeKey](input: Future[A]): Future[A] = {
    traversor.traverse[A](input) recoverWith {
      case e: CouldNotTraverseException => Future.failed(new CouldNotTransformException(e.typeKey, e))
    }
  }

  /** the execution context in which to run */
  protected implicit val executionContext: ExecutionContext

  /** the emblematic types to use in the recursive transformation */
  protected val emblematic: Emblematic = Emblematic.empty

  /** the custom transformers to use in the recursive transformation */
  protected val customTransformers: CustomTransformerPool = CustomTransformerPool.empty

  /** transforms a boolean */
  protected def transformBoolean(input: Future[Boolean]): Future[Boolean] = input

  /** transforms a char */
  protected def transformChar(input: Future[Char]): Future[Char] = input

  /** transforms a date-time */
  protected def transformDateTime(input: Future[DateTime]): Future[DateTime] = input

  /** transforms a double */
  protected def transformDouble(input: Future[Double]): Future[Double] = input

  /** transforms a float */
  protected def transformFloat(input: Future[Float]): Future[Float] = input

  /** transforms an int */
  protected def transformInt(input: Future[Int]): Future[Int] = input

  /** transforms a long */
  protected def transformLong(input: Future[Long]): Future[Long] = input

  /** transforms a string */
  protected def transformString(input: Future[String]): Future[String] = input

  private lazy val traversor = new Traversor {

    type TraverseInput[A] = A
    type TraverseResult[A] = A

    def traverseBoolean(input: Future[Boolean]): Future[Boolean] = transformBoolean(input)

    def traverseChar(input: Future[Char]): Future[Char] = transformChar(input)

    def traverseDateTime(input: Future[DateTime]): Future[DateTime] = transformDateTime(input)

    def traverseDouble(input: Future[Double]): Future[Double] = transformDouble(input)

    def traverseFloat(input: Future[Float]): Future[Float] = transformFloat(input)

    def traverseInt(input: Future[Int]): Future[Int] = transformInt(input)

    def traverseLong(input: Future[Long]): Future[Long] = transformLong(input)

    def traverseString(input: Future[String]): Future[String] = transformString(input)

    override protected implicit val executionContext = Transformer.this.executionContext
    override protected val emblematic = Transformer.this.emblematic

    override protected val customTraversors = {
      class VisCustomTraversor[A](val customTransformer: CustomTransformer[A]) extends CustomTraversor[A] {
        def apply[B <: A : TypeKey](input: Future[B]): Future[B] =
          customTransformer.apply[B](Transformer.this, input)
      }
      val transformerToTraversor = new TypeBoundFunction[Any, CustomTransformer, CustomTraversor] {
        def apply[A](transformer: CustomTransformer[A]): CustomTraversor[A] = new VisCustomTraversor(transformer)
      }
      customTransformers.mapValues(transformerToTraversor)
    }

    protected def stageEmblemProps[A <: HasEmblem : TypeKey](emblem: Emblem[A], futureA: Future[A])
    : Future[Iterable[PropInput[A, _]]] = {
      futureA map { a =>
        def propInput[B](prop: EmblemProp[A, B]) = (prop, prop.get(a))
        emblem.props.map { prop => propInput(prop) }
      }
    }

    protected def unstageEmblemProps[A <: HasEmblem : TypeKey](
      emblem: Emblem[A],
      result: Future[Iterable[PropResult[A, _]]])
    : Future[A] = {
      result map { propResults =>
        val builder = emblem.builder()
        propResults foreach { case (prop, propResult) => builder.setProp(prop, propResult) }
        builder.build()
      }
    }

    protected def stageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      domain: Future[Domain])
    : Future[Range] =
      domain map extractor.apply

    protected def unstageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      range: Future[Range])
    : Future[Domain] =
      range map { r =>
        try {
          extractor.inverse(r)
        } catch {
          case e: Exception => throw new ExtractorInverseException(r, typeKey[Domain], e)
        }
      }

    protected def stageOptionValue[A : TypeKey](input: Future[Option[A]]): Future[Iterable[A]] =
      input.map(_.toIterable)

    protected def unstageOptionValue[A : TypeKey](input: Future[Option[A]], result: Future[Iterable[A]])
    : Future[Option[A]] =
      result.map(_.headOption)

    protected def stageSetElements[A : TypeKey](input: Future[Set[A]]): Future[Iterable[A]] =
      input

    protected def unstageSetElements[A : TypeKey](input: Future[Set[A]], result: Future[Iterable[A]])
    : Future[Set[A]] =
      result.map(_.toSet)

    protected def stageListElements[A : TypeKey](input: Future[List[A]]): Future[Iterable[A]] =
      input

    protected def unstageListElements[A : TypeKey](input: Future[List[A]], result: Future[Iterable[A]])
    : Future[List[A]] =
      result.map(_.toList)

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
    def apply[B <: A : TypeKey](transformer: Transformer, input: Future[B]): Future[B]

  }

  /** a [[TypeKeyMap]] for [[CustomTransformer transformer functions]] */
  type CustomTransformerPool = TypeKeyMap[Any, CustomTransformer]

  object CustomTransformerPool {

    /** an empty map of [[CustomTransformer transformer functions]] */
    def empty: CustomTransformerPool = TypeKeyMap[Any, CustomTransformer]()
  }

}
