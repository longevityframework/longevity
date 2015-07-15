package emblem.traversors.sync

import emblem.TypeBoundFunction
import emblem.imports._
import emblem.reflectionUtil.makeTypeTag
import emblem.traversors.async.{ Traversor => AsyncTraversor }
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.concurrent.duration.Duration
import scala.reflect.runtime.universe.typeOf
import scala.util.Failure
import scala.util.Success
import scala.util.Try

// TODO investigate stall in test suite
// TODO investigate sporadic failure in TestDataGeneratorSpec

/** synchronously traverses a recursive data structure. the inputs and the outputs of the traversal are abstract
 * here, and specified by the implementing class. this forms a generic pattern for [[Visitor visiting]],
 * [[Generator generating]], and [[Transformer transforming]] data.
 * 
 * you can traverse arbritrary data to your liking by implementing the protected vals and defs in this
 * interface.
 */
trait Traversor {

  /** the input to a traversal step over type `A` */
  type TraverseInput[A]

  /** the output to a traversal step over type `A`*/
  type TraverseResult[A]

  /** a custom traversor over type `A` */
  trait CustomTraversor[A] {
    def apply[B <: A : TypeKey](input: TraverseInput[B]): TraverseResult[B]
  }

  /** a [[TypeKeyMap]] for [[CustomTraversor custom traversors]] */
  type CustomTraversorPool = TypeKeyMap[Any, CustomTraversor]

  object CustomTraversorPool {

    /** an empty map of [[CustomTraversor custom traversors]] */
    val empty: CustomTraversorPool = TypeKeyMap[Any, CustomTraversor]
  }

  /** traverses an object of any supported type.
   *
   * @tparam A the type of the object to traverse
   * @throws emblem.exceptions.CouldNotTraverseException when an unsupported type is encountered during the
   * traversal
   */
  def traverse[A : TypeKey](input: TraverseInput[A]): TraverseResult[A] = {
    val futureInput = Promise.successful(input).future
    val futureResult = asyncTraversor.traverse(futureInput)
    Await.result(futureResult, Duration.Inf)
  }

  /** an input for traversing an [[EmblemProp]] */
  protected type PropInput[A <: HasEmblem, B] = (EmblemProp[A, B], TraverseInput[B])

  /** an output for traversing an [[EmblemProp]] */
  protected type PropResult[A <: HasEmblem, B] = (EmblemProp[A, B], TraverseResult[B])

  /** the emblems to use in the recursive traversal */
  protected val emblemPool: EmblemPool = EmblemPool.empty

  /** the extractors to use in the recursive traversal */
  protected val extractorPool: ExtractorPool = ExtractorPool.empty

  /** the custom traversors to use in the recursive traversal */
  protected val customTraversors: CustomTraversorPool = CustomTraversorPool.empty

  /** traverses a boolean */
  protected def traverseBoolean(input: TraverseInput[Boolean]): TraverseResult[Boolean]

  /** traverses a char */
  protected def traverseChar(input: TraverseInput[Char]): TraverseResult[Char]

  /** traverses a double */
  protected def traverseDouble(input: TraverseInput[Double]): TraverseResult[Double]

  /** traverses a float */
  protected def traverseFloat(input: TraverseInput[Float]): TraverseResult[Float]

  /** traverses an int */
  protected def traverseInt(input: TraverseInput[Int]): TraverseResult[Int]

  /** traverses a long */
  protected def traverseLong(input: TraverseInput[Long]): TraverseResult[Long]

  /** traverses a string */
  protected def traverseString(input: TraverseInput[String]): TraverseResult[String]

  /** stages the traversal of a [[Emblem emblem's]] [[EmblemProp props]]
   * @tparam A the type of the [[HasEmblem]] object to traverse
   * @param emblem the emblem being traversed
   * @param input the input to the emblem traversal
   * @return an iteratable of inputs for the emblem props
   */
  protected def stageEmblemProps[A <: HasEmblem](
    emblem: Emblem[A],
    input: TraverseInput[A])
  : Iterable[PropInput[A, _]]

  /** unstages the traversal of a [[Emblem emblem's]] [[EmblemProp props]]
   * @tparam A the type of the [[HasEmblem]] object to traverse
   * @param emblem the emblem being traversed
   * @param an iterable of the outputs for the emblem props
   * @return the output for the emblem
   */
  protected def unstageEmblemProps[A <: HasEmblem](
    emblem: Emblem[A],
    result: Iterable[PropResult[A, _]])
  : TraverseResult[A]

  /** stages the traversal of a [[Extractor extractor]]
   * @tparam Range the range type for the extractor
   * @tparam Domain the domain type for the extractor
   * @param extractor the extractor being traversed
   * @param input the input to the extractor traversal
   * @return the input for traversing `Extractor.inverse`
   */
  protected def stageExtractor[Domain : TypeKey, Range](
    extractor: Extractor[Domain, Range],
    input: TraverseInput[Domain])
  : TraverseInput[Range]

  /** unstages the traversal of a [[Extractor extractor]]
   * @tparam Range the range type for the extractor
   * @tparam Domain the domain type for the extractor
   * @param extractor the extractor being traversed
   * @param rangeResult the result of traversing `Extractor.inverse`
   * @return the result of traversing the extractor
   */
  protected def unstageExtractor[Domain : TypeKey, Range](
    extractor: Extractor[Domain, Range],
    rangeResult: TraverseResult[Range])
  : TraverseResult[Domain]

  /** stages the traversal of an option's value
   * @tparam A the type of the option's value
   * @param input the input to traversing the option
   * @return an iterable of 0 or 1 inputs of the option's value. an empty iterable is returned to avoid
   * traversal into the option.
   */
  protected def stageOptionValue[A : TypeKey](
    input: TraverseInput[Option[A]])
  : Iterable[TraverseInput[A]]

  /** unstages the traversal of an option's value
   * @tparam A the type of the option's value
   * @param input the input to traversing the option
   * @param result an iterable of 0 or 1 results of the option's value. an empty iterable indicates that
   * traversal into the option has been avoided.
   */
  protected def unstageOptionValue[A : TypeKey](
    input: TraverseInput[Option[A]],
    result: Iterable[TraverseResult[A]])
  : TraverseResult[Option[A]]

  /** stages the traversal of an set's elements
   * @tparam A the type of the set elements
   * @param input the input to traversing the set
   * @return a iterable of inputs for the set's elements. an empty iterable is returned to avoid
   * traversal into the set.
   */
  protected def stageSetElements[A : TypeKey](
    input: TraverseInput[Set[A]])
  : Iterable[TraverseInput[A]]

  /** unstages the traversal of an set's elements
   * @tparam A the type of the set elements
   * @param input the input to traversing the set
   * @param result an iterable of results for the set's elements. an empty iterable indicates that traversal
   * into the set has been avoided
   * @return the result of traversing the set
   */
  protected def unstageSetElements[A : TypeKey](
    input: TraverseInput[Set[A]],
    result: Iterable[TraverseResult[A]])
  : TraverseResult[Set[A]]

  /** stages the traversal of an list's elements
   * @tparam A the type of the list elements
   * @param input the input to traversing the list
   * @return a iterable of inputs for the list's elements. an empty iterable is returned to avoid
   * traversal into the list.
   */
  protected def stageListElements[A : TypeKey](
    input: TraverseInput[List[A]])
  : Iterable[TraverseInput[A]]

  /** unstages the traversal of a list's elements
   * @tparam A the type of the list elements
   * @param input the input to traversing the list
   * @param result an iterable of results for the list's elements. an empty iterable indicates that traversal
   * into the list has been avoided
   * @return the result of travering the list
   */
  protected def unstageListElements[A : TypeKey](
    input: TraverseInput[List[A]],
    result: Iterable[TraverseResult[A]])
  : TraverseResult[List[A]]

  private def asyncTraversor = new AsyncTraversor {

    type TraverseInput[A] = Traversor.this.TraverseInput[A]
    type TraverseResult[A] = Traversor.this.TraverseResult[A]

    override protected val emblemPool: EmblemPool = Traversor.this.emblemPool
    override protected val extractorPool: ExtractorPool = Traversor.this.extractorPool

    private class CustomTraversorAdapter[A](val adaptee: Traversor.this.CustomTraversor[A])
    extends CustomTraversor[A] {
      def apply[B <: A : TypeKey](input: Future[TraverseInput[B]]): Future[TraverseResult[B]] =
        input map { i => adaptee(i) }
    }

    private val adaptCustomTraversor =
      new TypeBoundFunction[Any, Traversor.this.CustomTraversor, CustomTraversor] {
        def apply[A](adaptee: Traversor.this.CustomTraversor[A]) = new CustomTraversorAdapter[A](adaptee)
      }

    override protected val customTraversors: CustomTraversorPool =
      Traversor.this.customTraversors.mapValues(adaptCustomTraversor)

    protected def traverseBoolean(input: Future[TraverseInput[Boolean]]): Future[TraverseResult[Boolean]] =
      input.map(Traversor.this.traverseBoolean(_))

    protected def traverseChar(input: Future[TraverseInput[Char]]): Future[TraverseResult[Char]] =
      input.map(Traversor.this.traverseChar(_))

    protected def traverseDouble(input: Future[TraverseInput[Double]]): Future[TraverseResult[Double]] =
      input.map(Traversor.this.traverseDouble(_))

    protected def traverseFloat(input: Future[TraverseInput[Float]]): Future[TraverseResult[Float]] =
      input.map(Traversor.this.traverseFloat(_))

    protected def traverseInt(input: Future[TraverseInput[Int]]): Future[TraverseResult[Int]] =
      input.map(Traversor.this.traverseInt(_))

    protected def traverseLong(input: Future[TraverseInput[Long]]): Future[TraverseResult[Long]] =
      input.map(Traversor.this.traverseLong(_))

    protected def traverseString(input: Future[TraverseInput[String]]): Future[TraverseResult[String]] =
      input.map(Traversor.this.traverseString(_))

    protected def stageEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      futureInputA: Future[TraverseInput[A]])
    : Future[Iterable[PropInput[A, _]]] =
      futureInputA map { inputA => Traversor.this.stageEmblemProps(emblem, inputA) }

    protected def unstageEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      asyncResult: Future[Iterable[PropResult[A, _]]])
    : Future[TraverseResult[A]] =
      asyncResult map { result => Traversor.this.unstageEmblemProps(emblem, result) }

    protected def stageExtractor[Domain : TypeKey, Range](
      extractor: Extractor[Domain, Range],
      input: Future[TraverseInput[Domain]])
    : Future[TraverseInput[Range]] =
      input.map(Traversor.this.stageExtractor(extractor, _))

    protected def unstageExtractor[Domain : TypeKey, Range](
      extractor: Extractor[Domain, Range],
      rangeResult: Future[TraverseResult[Range]])
    : Future[TraverseResult[Domain]] =
      rangeResult.map(Traversor.this.unstageExtractor(extractor, _))

    protected def stageOptionValue[A : TypeKey](
      input: Future[TraverseInput[Option[A]]])
    : Future[Iterable[TraverseInput[A]]] =
      input.map(Traversor.this.stageOptionValue(_))

    protected def unstageOptionValue[A : TypeKey](
      input: Future[TraverseInput[Option[A]]],
      result: Future[Iterable[TraverseResult[A]]])
    : Future[TraverseResult[Option[A]]] =
      for (i <- input; r <- result) yield Traversor.this.unstageOptionValue(i, r)

    protected def stageSetElements[A : TypeKey](input: Future[TraverseInput[Set[A]]])
    : Future[Iterable[TraverseInput[A]]] =
      input.map(Traversor.this.stageSetElements(_))

    protected def unstageSetElements[A : TypeKey](
      input: Future[TraverseInput[Set[A]]],
      result: Future[Iterable[TraverseResult[A]]])
    : Future[TraverseResult[Set[A]]] =
      for (i <- input; r <- result) yield Traversor.this.unstageSetElements(i, r)

    protected def stageListElements[A : TypeKey](
      input: Future[TraverseInput[List[A]]])
    : Future[Iterable[TraverseInput[A]]] =
      input.map(Traversor.this.stageListElements(_))

    protected def unstageListElements[A : TypeKey](
      input: Future[TraverseInput[List[A]]],
      result: Future[Iterable[TraverseResult[A]]])
    : Future[TraverseResult[List[A]]] =
      for (i <- input; r <- result) yield Traversor.this.unstageListElements(i, r)

  }

}
