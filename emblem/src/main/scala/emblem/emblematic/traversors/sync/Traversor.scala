package emblem.emblematic.traversors.sync

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.emblematic.Emblem
import emblem.emblematic.EmblemProp
import emblem.emblematic.Emblematic
import emblem.emblematic.Extractor
import emblem.emblematic.Union
import emblem.emblematic.traversors.async.{ Traversor => AsyncTraversor }
import emblem.typeBound.TypeBoundFunction
import org.joda.time.DateTime
import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.Duration

/** synchronously traverses a recursive data structure. the inputs and the
 * outputs of the traversal are abstract here, and specified by the implementing
 * class. this forms a generic pattern for [[Visitor visiting]],
 * [[Generator generating]], and [[Transformer transforming]] data.
 * 
 * you can traverse arbritrary data to your liking by implementing the
 * protected vals and defs in this interface.
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
    val futureInput = Future.successful(input)
    val futureResult = asyncTraversor.traverse(futureInput)
    Await.result(futureResult, Duration.Inf)
  }

  /** an input for traversing an [[emblem.emblematic.EmblemProp EmblemProp]] */
  protected type PropInput[A, B] = (EmblemProp[A, B], TraverseInput[B])

  /** an output for traversing an [[emblem.emblematic.EmblemProp EmblemProp]] */
  protected type PropResult[A, B] = (EmblemProp[A, B], TraverseResult[B])

  /** the emblematic types to use in the recursive traversal */
  protected val emblematic: Emblematic = Emblematic()

  /** the custom traversors to use in the recursive traversal */
  protected val customTraversors: CustomTraversorPool = CustomTraversorPool.empty

  /** traverses a boolean */
  protected def traverseBoolean(input: TraverseInput[Boolean]): TraverseResult[Boolean]

  /** traverses a char */
  protected def traverseChar(input: TraverseInput[Char]): TraverseResult[Char]

  /** traverses a date-time */
  protected def traverseDateTime(input: TraverseInput[DateTime]): TraverseResult[DateTime]

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

  /** decodes and returns a type key for the union constituent from the input
   *
   * @tparam A the type of the [[emblem.emblematic.Union Union]] object to traverse
   * @param union the union
   * @param input the input to decode
   * @return the constituent type key
   */
  protected def constituentTypeKey[A : TypeKey](union: Union[A], input: TraverseInput[A]): TypeKey[_ <: A]

  /** stages the traversal of a [[emblem.emblematic.Union Union]]
   *
   * @tparam A the type of the union
   * @param input the input to traversing the union
   * @return an iterable of 0 or 1 inputs of the union value. an empty
   * iterable is returned to avoid traversal into the union.
   */
  protected def stageUnion[A : TypeKey, B <: A : TypeKey](union: Union[A], input: TraverseInput[A])
  : Iterable[TraverseInput[B]]

  /** unstages the traversal of a [[emblem.emblematic.Union Union]]
   *
   * @tparam A the type of the union
   * @param input the input to traversing the union
   * @param result an iterable of 0 or 1 results of the union's value. an empty
   * iterable indicates that traversal into the union has been avoided.
   * @return the result of traversing the union
   */
  protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
    union: Union[A],
    input: TraverseInput[A],
    result: Iterable[TraverseResult[B]])
  : TraverseResult[A]

  /** stages the traversal of an [[emblem.emblematic.Emblem Emblem's]]
   * [[emblem.emblematic.EmblemProp props]]
   * 
   * @tparam A the type of the object to traverse
   * @param emblem the emblem being traversed
   * @param input the input to the emblem traversal
   * @return an iteratable of inputs for the emblem props
   */
  protected def stageEmblemProps[A : TypeKey](
    emblem: Emblem[A],
    input: TraverseInput[A])
  : Iterable[PropInput[A, _]]

  /** unstages the traversal of an [[emblem.emblematic.Emblem Emblem's]]
   * [[emblem.emblematic.EmblemProp props]]
   * 
   * @tparam A the type of the object to traverse
   * @param emblem the emblem being traversed
   * @param an iterable of the outputs for the emblem props
   * @return the output for the emblem
   */
  protected def unstageEmblemProps[A : TypeKey](
    emblem: Emblem[A],
    result: Iterable[PropResult[A, _]])
  : TraverseResult[A]

  /** stages the traversal of an [[emblem.emblematic.Extractor Extractor]]
   * 
   * @tparam Range the range type for the extractor
   * @tparam Domain the domain type for the extractor
   * @param extractor the extractor being traversed
   * @param input the input to the extractor traversal
   * @return the input for traversing `Extractor.inverse`
   */
  protected def stageExtractor[Domain : TypeKey, Range : TypeKey](
    extractor: Extractor[Domain, Range],
    input: TraverseInput[Domain])
  : TraverseInput[Range]

  /** unstages the traversal of an [[emblem.emblematic.Extractor Extractor]]
   * 
   * @tparam Range the range type for the extractor
   * @tparam Domain the domain type for the extractor
   * @param extractor the extractor being traversed
   * @param rangeResult the result of traversing `Extractor.inverse`
   * @return the result of traversing the extractor
   */
  protected def unstageExtractor[Domain : TypeKey, Range : TypeKey](
    extractor: Extractor[Domain, Range],
    rangeResult: TraverseResult[Range])
  : TraverseResult[Domain]

  /** stages the traversal of an option's value
   * 
   * @tparam A the type of the option's value
   * @param input the input to traversing the option
   * @return an iterable of 0 or 1 inputs of the option's value. an empty
   * iterable is returned to avoid traversal into the option.
   */
  protected def stageOptionValue[A : TypeKey](
    input: TraverseInput[Option[A]])
  : Iterable[TraverseInput[A]]

  /** unstages the traversal of an option's value
   * 
   * @tparam A the type of the option's value
   * @param input the input to traversing the option
   * @param result an iterable of 0 or 1 results of the option's value. an
   * empty iterable indicates that traversal into the option has been avoided
   */
  protected def unstageOptionValue[A : TypeKey](
    input: TraverseInput[Option[A]],
    result: Iterable[TraverseResult[A]])
  : TraverseResult[Option[A]]

  /** stages the traversal of an set's elements
   * 
   * @tparam A the type of the set elements
   * @param input the input to traversing the set
   * @return a iterable of inputs for the set's elements. an empty iterable is
   * returned to avoid traversal into the set.
   */
  protected def stageSetElements[A : TypeKey](
    input: TraverseInput[Set[A]])
  : Iterable[TraverseInput[A]]

  /** unstages the traversal of an set's elements
   * 
   * @tparam A the type of the set elements
   * @param input the input to traversing the set
   * @param result an iterable of results for the set's elements. an empty
   * iterable indicates that traversal into the set has been avoided
   * @return the result of traversing the set
   */
  protected def unstageSetElements[A : TypeKey](
    input: TraverseInput[Set[A]],
    result: Iterable[TraverseResult[A]])
  : TraverseResult[Set[A]]

  /** stages the traversal of an list's elements
   * 
   * @tparam A the type of the list elements
   * @param input the input to traversing the list
   * @return a iterable of inputs for the list's elements. an empty iterable is
   * returned to avoid traversal into the list.
   */
  protected def stageListElements[A : TypeKey](
    input: TraverseInput[List[A]])
  : Iterable[TraverseInput[A]]

  /** unstages the traversal of a list's elements
   * 
   * @tparam A the type of the list elements
   * @param input the input to traversing the list
   * @param result an iterable of results for the list's elements. an empty
   * iterable indicates that traversal into the list has been avoided
   * @return the result of travering the list
   */
  protected def unstageListElements[A : TypeKey](
    input: TraverseInput[List[A]],
    result: Iterable[TraverseResult[A]])
  : TraverseResult[List[A]]

  private def asyncTraversor = new AsyncTraversor {

    type TraverseInput[A] = Traversor.this.TraverseInput[A]
    type TraverseResult[A] = Traversor.this.TraverseResult[A]

    override protected implicit val executionContext = ExecutionContext.Implicits.global
    override protected val emblematic = Traversor.this.emblematic

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

    override protected def traverseBoolean(
      input: Future[TraverseInput[Boolean]])
    : Future[TraverseResult[Boolean]] =
      input.map(Traversor.this.traverseBoolean)

    override protected def traverseChar(
      input: Future[TraverseInput[Char]])
    : Future[TraverseResult[Char]] =
      input.map(Traversor.this.traverseChar)

    override protected def traverseDateTime(
      input: Future[TraverseInput[DateTime]])
    : Future[TraverseResult[DateTime]] =
      input.map(Traversor.this.traverseDateTime)

    override protected def traverseDouble(
      input: Future[TraverseInput[Double]])
    : Future[TraverseResult[Double]] =
      input.map(Traversor.this.traverseDouble)

    override protected def traverseFloat(
      input: Future[TraverseInput[Float]])
    : Future[TraverseResult[Float]] =
      input.map(Traversor.this.traverseFloat)

    override protected def traverseInt(
      input: Future[TraverseInput[Int]])
    : Future[TraverseResult[Int]] =
      input.map(Traversor.this.traverseInt)

    override protected def traverseLong(
      input: Future[TraverseInput[Long]])
    : Future[TraverseResult[Long]] =
      input.map(Traversor.this.traverseLong)

    override protected def traverseString(
      input: Future[TraverseInput[String]])
    : Future[TraverseResult[String]] =
      input.map(Traversor.this.traverseString)

    override protected def constituentTypeKey[A : TypeKey](
      union: Union[A],
      input: TraverseInput[A])
    : TypeKey[_ <: A] =
      Traversor.this.constituentTypeKey(union, input)

    override protected def stageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: Future[TraverseInput[A]])
    : Future[Iterable[TraverseInput[B]]] =
      input map { i => Traversor.this.stageUnion(union, i) }

    override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: Future[TraverseInput[A]],
      result: Future[Iterable[TraverseResult[B]]])
    : Future[TraverseResult[A]] =
      for (i <- input; r <- result) yield Traversor.this.unstageUnion(union, i, r)

    override protected def stageEmblemProps[A : TypeKey](
      emblem: Emblem[A],
      futureInputA: Future[TraverseInput[A]])
    : Future[Iterable[PropInput[A, _]]] =
      futureInputA map { inputA => Traversor.this.stageEmblemProps(emblem, inputA) }

    override protected def unstageEmblemProps[A : TypeKey](
      emblem: Emblem[A],
      asyncResult: Future[Iterable[PropResult[A, _]]])
    : Future[TraverseResult[A]] =
      asyncResult map { result => Traversor.this.unstageEmblemProps(emblem, result) }

    override protected def stageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      input: Future[TraverseInput[Domain]])
    : Future[TraverseInput[Range]] =
      input.map(Traversor.this.stageExtractor(extractor, _))

    override protected def unstageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      rangeResult: Future[TraverseResult[Range]])
    : Future[TraverseResult[Domain]] =
      rangeResult.map(Traversor.this.unstageExtractor(extractor, _))

    override protected def stageOptionValue[A : TypeKey](
      input: Future[TraverseInput[Option[A]]])
    : Future[Iterable[TraverseInput[A]]] =
      input.map(Traversor.this.stageOptionValue(_))

    override protected def unstageOptionValue[A : TypeKey](
      input: Future[TraverseInput[Option[A]]],
      result: Future[Iterable[TraverseResult[A]]])
    : Future[TraverseResult[Option[A]]] =
      for (i <- input; r <- result) yield Traversor.this.unstageOptionValue(i, r)

    override protected def stageSetElements[A : TypeKey](input: Future[TraverseInput[Set[A]]])
    : Future[Iterable[TraverseInput[A]]] =
      input.map(Traversor.this.stageSetElements(_))

    override protected def unstageSetElements[A : TypeKey](
      input: Future[TraverseInput[Set[A]]],
      result: Future[Iterable[TraverseResult[A]]])
    : Future[TraverseResult[Set[A]]] =
      for (i <- input; r <- result) yield Traversor.this.unstageSetElements(i, r)

    override protected def stageListElements[A : TypeKey](
      input: Future[TraverseInput[List[A]]])
    : Future[Iterable[TraverseInput[A]]] =
      input.map(Traversor.this.stageListElements(_))

    override protected def unstageListElements[A : TypeKey](
      input: Future[TraverseInput[List[A]]],
      result: Future[Iterable[TraverseResult[A]]])
    : Future[TraverseResult[List[A]]] =
      for (i <- input; r <- result) yield Traversor.this.unstageListElements(i, r)

  }

}
