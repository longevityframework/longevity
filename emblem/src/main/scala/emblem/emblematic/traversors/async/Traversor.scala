package emblem.emblematic.traversors.async

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.emblematic.Emblem
import emblem.emblematic.EmblemProp
import emblem.emblematic.Emblematic
import emblem.emblematic.Union
import emblem.exceptions.CouldNotTraverseException
import emblem.typeKey
import org.joda.time.DateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.reflect.runtime.universe.typeOf

/** asynchronously traverses a recursive data structure. the inputs and the
 * outputs of the traversal are abstract here, and specified by the implementing
 * class. this forms a generic pattern for
 * [[emblem.emblematic.traversors.sync.Visitor visiting]],
 * [[emblem.emblematic.traversors.sync.Generator generating]], and [[Transformer
 * transforming]] data.
 * 
 * you can traverse arbritrary data to your liking by implementing the protected
 * vals and defs in this interface.
 */
trait Traversor {

  /** the input to a traversal step over type `A` */
  type TraverseInput[A]

  /** the output to a traversal step over type `A`*/
  type TraverseResult[A]

  /** a custom traversor over type `A` */
  trait CustomTraversor[A] {
    def apply[B <: A : TypeKey](input: Future[TraverseInput[B]]): Future[TraverseResult[B]]
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
   * @param input the asynchronous object to traverse
   * @return the asynchronous result of the traversal. returns a failed future containing a
   * emblem.exceptions.CouldNotTraverseException when an unsupported type is encountered during the
   * traversal
   */
  def traverse[A : TypeKey](input: Future[TraverseInput[A]]): Future[TraverseResult[A]] =
    tryTraverseAny[A](input) getOrElse Future.failed(new CouldNotTraverseException(typeKey[A]))

  /** an input for traversing an [[emblem.emblematic.EmblemProp EmblemProp]] */
  protected type PropInput[A, B] = (EmblemProp[A, B], TraverseInput[B])

  /** an output for traversing an [[emblem.emblematic.EmblemProp EmblemProp]] */
  protected type PropResult[A, B] = (EmblemProp[A, B], TraverseResult[B])

  /** the execution context in which to run */
  protected implicit val executionContext: ExecutionContext

  /** the emblematic types to use in the recursive traversal */
  protected val emblematic: Emblematic = Emblematic()

  /** the custom traversors to use in the recursive traversal */
  protected val customTraversors: CustomTraversorPool = CustomTraversorPool.empty

  /** traverses a boolean */
  protected def traverseBoolean(input: Future[TraverseInput[Boolean]]): Future[TraverseResult[Boolean]]

  /** traverses a char */
  protected def traverseChar(input: Future[TraverseInput[Char]]): Future[TraverseResult[Char]]

  /** traverses a date-time */
  protected def traverseDateTime(input: Future[TraverseInput[DateTime]]): Future[TraverseResult[DateTime]]

  /** traverses a double */
  protected def traverseDouble(input: Future[TraverseInput[Double]]): Future[TraverseResult[Double]]

  /** traverses a float */
  protected def traverseFloat(input: Future[TraverseInput[Float]]): Future[TraverseResult[Float]]

  /** traverses an int */
  protected def traverseInt(input: Future[TraverseInput[Int]]): Future[TraverseResult[Int]]

  /** traverses a long */
  protected def traverseLong(input: Future[TraverseInput[Long]]): Future[TraverseResult[Long]]

  /** traverses a string */
  protected def traverseString(input: Future[TraverseInput[String]]): Future[TraverseResult[String]]

  private type TraversorFunction[A] = (Future[TraverseInput[A]]) => Future[TraverseResult[A]]

  private val basicTraversors =
    TypeKeyMap[Any, TraversorFunction] +
    traverseBoolean _ +
    traverseChar _ +
    traverseDateTime _ +
    traverseDouble _ +
    traverseFloat _ +
    traverseInt _ +
    traverseLong _ +
    traverseString _

  // custom generators have to come first. after that order is immaterial
  private def tryTraverseAny[A : TypeKey](input: Future[TraverseInput[A]]): Option[Future[TraverseResult[A]]] =
    tryTraverseCustom(input) orElse
    tryTraverseUnion(input) orElse
    tryTraverseEmblem(input) orElse
    tryTraverseOption(input) orElse
    tryTraverseSet(input) orElse
    tryTraverseList(input) orElse
    tryTraverseBasic(input)

  private def tryTraverseCustom[A : TypeKey](input: Future[TraverseInput[A]])
  : Option[Future[TraverseResult[A]]] = {
    val keyOpt: Option[TypeKey[_ >: A]] = {
      val matchingTraversorKeys =
        customTraversors.keys.filter(_.castToLowerBound[A].nonEmpty).toSeq.asInstanceOf[Seq[TypeKey[_ >: A]]]
      val tightestToLoosest = matchingTraversorKeys.sortWith(_ <:< _)
      tightestToLoosest.headOption
    }
    def getCustomTraversor[B >: A : TypeKey]: CustomTraversor[B] = customTraversors(typeKey[B])
    keyOpt map { key => getCustomTraversor(key).apply[A](input) }
  }

  private def tryTraverseUnion[A : TypeKey](input: Future[TraverseInput[A]])
  : Option[Future[TraverseResult[A]]] = {
    emblematic.unions.get[A] map { union => traverseFromUnion[A](union, input) }
  }

  private def traverseFromUnion[A : TypeKey](
    union: Union[A],
    input: Future[TraverseInput[A]])
  : Future[TraverseResult[A]] = {
    input flatMap { i =>
      val typeKeyA = typeKey[A]
      def traverseConstituent[B <: A : TypeKey] = {
        val typeKeyB = typeKey[B]

        val promise = Promise[TraverseResult[A]]()

        def completeIterableTraverseInput(iterableTraverseInputB: Iterable[TraverseInput[B]]): Unit = {

          val iterableFutureTraverseResultB = iterableTraverseInputB map { traverseInputB =>
            val futureTraverseInput = Future.successful(traverseInputB)
            traverse[B](futureTraverseInput)
          }
          val futureIterableTraverseResultB = Future.sequence(iterableFutureTraverseResultB)

          val futureTraverseResultA =
            unstageUnion(union, input, futureIterableTraverseResultB)(typeKeyA, typeKeyB)
          promise.completeWith(futureTraverseResultA)
        }

        val futureIterableTraverseInputB = stageUnion[A, B](union, input)
        futureIterableTraverseInputB onSuccess { case i => completeIterableTraverseInput(i) }
        futureIterableTraverseInputB onFailure { case e => promise.failure(e) }
        promise.future

      }

      val typeKeyB = constituentTypeKey(union, i)
      if (! union.constituentKeys.contains(typeKeyB)) throw new ClassCastException
      traverseConstituent(typeKeyB).asInstanceOf[Future[TraverseResult[A]]]
    }
  }

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
  protected def stageUnion[A : TypeKey, B <: A : TypeKey](
    union: Union[A],
    input: Future[TraverseInput[A]])
  : Future[Iterable[TraverseInput[B]]]

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
    input: Future[TraverseInput[A]],
    result: Future[Iterable[TraverseResult[B]]])
  : Future[TraverseResult[A]]

  private def tryTraverseEmblem[A : TypeKey](input: Future[TraverseInput[A]])
  : Option[Future[TraverseResult[A]]] = {
    emblematic.emblems.get(typeKey[A]) map { emblem => traverseEmblem(emblem, input) }
  }

  private def traverseEmblem[A : TypeKey](
    emblem: Emblem[A],
    input: Future[TraverseInput[A]])
  : Future[TraverseResult[A]] = {

    val promise = Promise[TraverseResult[A]]()

    def completeIterablePropInput(iterablePropInput: Iterable[PropInput[A, _]]): Unit = {
      val iterableFuturePropResult: Iterable[Future[PropResult[A, _]]] = iterablePropInput map {
        case (prop, input) =>
          val futureInput = Future.successful(input)
          traverseEmblemProp(emblem, prop, futureInput) map { result => (prop, result) }
      }
      val futureIterablePropResult = Future.sequence(iterableFuturePropResult)
      val futureTraverseResult = unstageEmblemProps(emblem, input, futureIterablePropResult)
      promise.completeWith(futureTraverseResult)
    }

    val futureIterablePropInput = stageEmblemProps(emblem, input)
    futureIterablePropInput onSuccess { case i => completeIterablePropInput(i) }
    futureIterablePropInput onFailure { case e => promise.failure(e) }
    promise.future
  }

  private def traverseEmblemProp[A : TypeKey, B](
    emblem: Emblem[A],
    prop: EmblemProp[A, B],
    input: Future[TraverseInput[B]])
  : Future[TraverseResult[B]] = {
    traverse(input)(prop.typeKey)
  }

  /** stages the traversal of an [[emblem.emblematic.Emblem Emblem's]]
   * [[emblem.emblematic.EmblemProp props]]
   * 
   * @tparam A the type of the object to traverse
   * @param emblem the emblem being traversed
   * @param input the input to the emblem traversal
   * @return an iterable of inputs for the emblem props
   */
  protected def stageEmblemProps[A : TypeKey](emblem: Emblem[A], input: Future[TraverseInput[A]])
  : Future[Iterable[PropInput[A, _]]]

  /** unstages the traversal of an [[emblem.emblematic.Emblem Emblem's]]
   * [[emblem.emblematic.EmblemProp props]]
   * 
   * @tparam A the type of the object to traverse
   * @param emblem the emblem being traversed
   * @param input the input to the emblem traversal
   * @param result an iterable of the outputs for the emblem props
   * @return the output for the emblem
   */
  protected def unstageEmblemProps[A : TypeKey](
    emblem: Emblem[A],
    input: Future[TraverseInput[A]],
    result: Future[Iterable[PropResult[A, _]]])
  : Future[TraverseResult[A]]

  // TODO pt-88571474: remove code duplication with option/set/list, generalize to other kinds of "collections"

  private def tryTraverseOption[OptionA : TypeKey](input: Future[TraverseInput[OptionA]])
  : Option[Future[TraverseResult[OptionA]]] = {
    val keyOption = optionElementTypeKeyOption(typeKey[OptionA])
    def doTraverse[A : TypeKey] = traverseOption(input.asInstanceOf[Future[TraverseInput[Option[A]]]])
    keyOption map { key => doTraverse(key).asInstanceOf[Future[TraverseResult[OptionA]]] }
  }

  // returns a `Some` containing the enclosing type of the option whenever the supplied type argument `A`
  // is an Option. otherwise returns `None`.
  private def optionElementTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] = {
    val key = typeKey[A]
    if (key =:= typeKey[None.type]) {
      Some(typeKey[Nothing])
    } else if (key <:< typeKey[Option[Any]]) {
      Some(key.typeArgs.head) 
    } else {
      None
    }
  }

  private[traversors] def traverseOption[A : TypeKey](
    futureTraverseInputOption: Future[TraverseInput[Option[A]]])
  : Future[TraverseResult[Option[A]]] = {
    val promise = Promise[TraverseResult[Option[A]]]()

    def completeIterableTraverseInput(iterableTraverseInput: Iterable[TraverseInput[A]]): Unit = {
      val iterableFutureTraverseResult = iterableTraverseInput map { traverseInput =>
        val futureTraverseInput = Future.successful(traverseInput)
        traverse[A](futureTraverseInput)
      }
      val futureIterableTraverseResult = Future.sequence(iterableFutureTraverseResult)
      val futureTraverseResultOption = unstageOptionValue(futureTraverseInputOption, futureIterableTraverseResult)
      promise.completeWith(futureTraverseResultOption)
    }

    val futureIterableTraverseInput = stageOptionValue[A](futureTraverseInputOption)
    futureIterableTraverseInput onSuccess { case i => completeIterableTraverseInput(i) }
    futureIterableTraverseInput onFailure { case e => promise.failure(e) }
    promise.future
  }

  /** stages the traversal of an option's value
   * 
   * @tparam A the type of the option's value
   * @param input the input to traversing the option
   * @return an iterable of 0 or 1 inputs of the option's value. an empty
   * iterable is returned to avoid traversal into the option.
   */
  protected def stageOptionValue[A : TypeKey](
    input: Future[TraverseInput[Option[A]]])
  : Future[Iterable[TraverseInput[A]]]

  /** unstages the traversal of an option's value
   * 
   * @tparam A the type of the option's value
   * @param input the input to traversing the option
   * @param result an iterable of 0 or 1 results of the option's value. an empty
   * iterable indicates that traversal into the option has been avoided.
   * @return the result of traversing the option
   */
  protected def unstageOptionValue[A : TypeKey](
    input: Future[TraverseInput[Option[A]]],
    result: Future[Iterable[TraverseResult[A]]])
  : Future[TraverseResult[Option[A]]]

  private def tryTraverseSet[SetA : TypeKey](input: Future[TraverseInput[SetA]])
  : Option[Future[TraverseResult[SetA]]] = {
    val keyOption = setElementTypeKeyOption(typeKey[SetA])
    def doTraverse[A : TypeKey] = traverseSet(input.asInstanceOf[Future[TraverseInput[Set[A]]]])
    keyOption map { k => doTraverse(k).asInstanceOf[Future[TraverseResult[SetA]]] }
  }

  // returns a `Some` containing the enclosing type of the set whenever the supplied type argument `A`
  // is a Set. otherwise returns `None`.
  private def setElementTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] =
    if (typeKey[A].tpe <:< typeOf[Set[_]]) Some(typeKey[A].typeArgs.head) else None

  private def traverseSet[A : TypeKey](futureTraverseInputSet: Future[TraverseInput[Set[A]]])
  : Future[TraverseResult[Set[A]]] = {
    val promise = Promise[TraverseResult[Set[A]]]()

    def completeIterableTraverseInput(iterableTraverseInput: Iterable[TraverseInput[A]]): Unit = {
      val iterableFutureTraverseResult = iterableTraverseInput map { traverseInput =>
        val futureTraverseInput = Promise.successful(traverseInput).future
        traverse[A](futureTraverseInput)
      }
      val futureIterableTraverseResult = Future.sequence(iterableFutureTraverseResult)
      val futureTraverseResultSet = unstageSetElements(futureTraverseInputSet, futureIterableTraverseResult)
      promise.completeWith(futureTraverseResultSet)
    }

    val futureIterableTraverseInput = stageSetElements[A](futureTraverseInputSet)
    futureIterableTraverseInput onSuccess { case i => completeIterableTraverseInput(i) }
    futureIterableTraverseInput onFailure { case e => promise.failure(e) }
    promise.future
  }

  /** stages the traversal of a set's elements
   * 
   * @tparam A the type of the set elements
   * @param input the input to traversing the set
   * @return a iterable of inputs for the set's elements. an empty iterable is
   * returned to avoid traversal into the set.
   */
  protected def stageSetElements[A : TypeKey](
    input: Future[TraverseInput[Set[A]]])
  : Future[Iterable[TraverseInput[A]]]

  /** unstages the traversal of an set's elements
   * 
   * @tparam A the type of the set elements
   * @param input the input to traversing the set
   * @param result an iterable of results for the set's elements. an empty
   * iterable indicates that traversal into the set has been avoided
   * @return the result of travering the set
   */
  protected def unstageSetElements[A : TypeKey](
    input: Future[TraverseInput[Set[A]]],
    result: Future[Iterable[TraverseResult[A]]])
  : Future[TraverseResult[Set[A]]]

  private def tryTraverseList[ListA : TypeKey](input: Future[TraverseInput[ListA]])
  : Option[Future[TraverseResult[ListA]]] = {
    val keyOption = listElementTypeKeyOption(typeKey[ListA])
    def doTraverse[A : TypeKey] = traverseList(input.asInstanceOf[Future[TraverseInput[List[A]]]])
    keyOption map { k => doTraverse(k).asInstanceOf[Future[TraverseResult[ListA]]] }
  }

  // returns a `Some` containing the enclosing type of the list whenever the supplied type argument `A`
  // is a List. otherwise returns `None`.
  private def listElementTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] = {
    val key = typeKey[A]
    if (key =:= typeKey[Nil.type]) {
      Some(typeKey[Nothing])
    } else if (key <:< typeOf[List[_]]) {
      Some(key.typeArgs.head)
    } else None
  }

  private def traverseList[A : TypeKey](futureTraverseInputList: Future[TraverseInput[List[A]]])
  : Future[TraverseResult[List[A]]] = {
    val promise = Promise[TraverseResult[List[A]]]()

    def completeIterableTraverseInput(iterableTraverseInput: Iterable[TraverseInput[A]]): Unit = {
      val iterableFutureTraverseResult = iterableTraverseInput map { traverseInput =>
        val futureTraverseInput = Promise.successful(traverseInput).future
        traverse[A](futureTraverseInput)
      }
      val futureIterableTraverseResult = Future.sequence(iterableFutureTraverseResult)
      val futureTraverseResultList = unstageListElements(futureTraverseInputList, futureIterableTraverseResult)
      promise.completeWith(futureTraverseResultList)
    }

    val futureIterableTraverseInput = stageListElements[A](futureTraverseInputList)
    futureIterableTraverseInput onSuccess { case i => completeIterableTraverseInput(i) }
    futureIterableTraverseInput onFailure { case e => promise.failure(e) }
    promise.future
  }

  /** stages the traversal of an list's elements
   * 
   * @tparam A the type of the list elements
   * @param input the input to traversing the list
   * @return a iterable of inputs for the list's elements. an empty iterable is
   * returned to avoid traversal into the list.
   */
  protected def stageListElements[A : TypeKey](
    input: Future[TraverseInput[List[A]]])
  : Future[Iterable[TraverseInput[A]]]

  /** unstages the traversal of a list's elements
   * 
   * @tparam A the type of the list elements
   * @param input the input to traversing the list
   * @param result an iterable of results for the list's elements. an empty
   * iterable indicates that traversal into the list has been avoided
   * @return the result of travering the list
   */
  protected def unstageListElements[A : TypeKey](
    input: Future[TraverseInput[List[A]]],
    result: Future[Iterable[TraverseResult[A]]])
  : Future[TraverseResult[List[A]]]

  private def tryTraverseBasic[Basic : TypeKey](input: Future[TraverseInput[Basic]])
  : Option[Future[TraverseResult[Basic]]] =
    basicTraversors.get[Basic] map { traversor => traversor(input) }

}
