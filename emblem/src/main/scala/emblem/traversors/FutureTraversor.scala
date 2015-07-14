package emblem.traversors

import emblem.imports._
import emblem.ExtractorFor
import emblem.exceptions.CouldNotTraverseException
import emblem.reflectionUtil.makeTypeTag
import scala.reflect.runtime.universe.typeOf
import scala.concurrent.Future
import scala.concurrent.Promise
import rx.lang.scala.Observable
import scala.concurrent.ExecutionContext.Implicits.global

// TODO update comments here

// TODO
//   - rename existing to SyncTrav etc
//   - move existing into sync subpackage
//   - rewrite Traversor in terms of FutureTraversor
//   - rewrite Transformer in terms of FutureTransformer
//   - async versions for the rest of the traversors
//   - always use iterator for the option stuff
//   - rerun scaladoc

/** recursively traverses a data structure by type. the inputs and the outputs of the traversal are abstract
 * here, and specified by the implementing class. this forms a generic pattern for [[Visitor visiting]],
 * [[Generator generating]], and [[Transformer transforming]] data.
 * 
 * you can traverse arbritrary data to your liking by implementing the protected vals and defs in this
 * interface. as of yet, i haven't been able to generate the scaladoc for those protected methods.
 * sorry about that.
 */
trait FutureTraversor {

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
   * @throws emblem.exceptions.CouldNotTraverseException when an unsupported type is encountered during the
   * traversal
   */
  def traverse[A : TypeKey](input: Future[TraverseInput[A]]): Future[TraverseResult[A]] =
    traverseAnyOption[A](input) getOrElse {
      throw new CouldNotTraverseException(typeKey[A])
    }

  /** an input for traversing an [[EmblemProp]] */
  protected type PropInput[A <: HasEmblem, B] = (EmblemProp[A, B], Future[TraverseInput[B]])

  /** an output for traversing an [[EmblemProp]] */
  protected type PropResult[A <: HasEmblem, B] = (EmblemProp[A, B], Future[TraverseResult[B]])

  /** the emblems to use in the recursive traversal */
  protected val emblemPool: EmblemPool = EmblemPool.empty

  /** the extractors to use in the recursive traversal */
  protected val extractorPool: ExtractorPool = ExtractorPool.empty

  /** the custom traversors to use in the recursive traversal */
  protected val customTraversors: CustomTraversorPool = CustomTraversorPool.empty

  /** traverses a boolean */
  protected def traverseBoolean(input: Future[TraverseInput[Boolean]]): Future[TraverseResult[Boolean]]

  /** traverses a char */
  protected def traverseChar(input: Future[TraverseInput[Char]]): Future[TraverseResult[Char]]

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

  /** stages the traversal of a [[Emblem emblem's]] [[EmblemProp props]]
   * @tparam A the type of the [[HasEmblem]] object to traverse
   * @param emblem the emblem being traversed
   * @param input the input to the emblem traversal
   * @return an iterator of inputs for the emblem props
   */
  protected def stageEmblemProps[A <: HasEmblem](
    emblem: Emblem[A],
    input: Future[TraverseInput[A]])
  : Observable[PropInput[A, _]]

  /** unstages the traversal of a [[Emblem emblem's]] [[EmblemProp props]]
   * @tparam A the type of the [[HasEmblem]] object to traverse
   * @param emblem the emblem being traversed
   * @param input the input to the emblem traversal
   * @param an iterator of the outputs for the emblem props
   * @return the output for the emblem
   */
  protected def unstageEmblemProps[A <: HasEmblem](
    emblem: Emblem[A],
    input: Future[TraverseInput[A]],
    result: Observable[PropResult[A, _]])
  : Future[TraverseResult[A]]

  /** stages the traversal of a [[Extractor extractor]]
   * @tparam Range the range type for the extractor
   * @tparam Domain the domain type for the extractor
   * @param extractor the extractor being traversed
   * @param input the input to the extractor traversal
   * @return the input for traversing `Extractor.inverse`
   */
  protected def stageExtractor[Domain : TypeKey, Range](
    extractor: Extractor[Domain, Range],
    input: Future[TraverseInput[Domain]])
  : Future[TraverseInput[Range]]

  /** unstages the traversal of a [[Extractor extractor]]
   * @tparam Range the range type for the extractor
   * @tparam Domain the domain type for the extractor
   * @param extractor the extractor being traversed
   * @param domainResult the result of traversing `Extractor.inverse`
   * @return the result of traversing the extractor
   */
  protected def unstageExtractor[Domain : TypeKey, Range](
    extractor: Extractor[Domain, Range],
    rangeResult: Future[TraverseResult[Range]])
  : Future[TraverseResult[Domain]]

  /** stages the traversal of an option's value
   * @tparam A the type of the option's value
   * @param input the input to traversing the option
   * @return an iterator of 0 or 1 inputs of the option's value. an empty iterator is returned to avoid
   * traversal into the option. we use an option here to stand in for an iterator of 0 or 1 values. note that
   * this usage of an option is different from the kind of option we are traversing
   */
  protected def stageOptionValue[A : TypeKey](
    input: Future[TraverseInput[Option[A]]])
  : Observable[TraverseInput[A]]

  /** unstages the traversal of an option's value
   * @tparam A the type of the option's value
   * @param input the input to traversing the option
   * @param result an iterator of 0 or 1 results of the option's value. an empty iterator indicates that
   * traversal into the option has been avoided. we use an option here to stand in for an iterator of 0 or 1
   * values. note that this usage of an option is different from the kind of option we are traversing
   * @return the result of traversing the option
   */
  protected def unstageOptionValue[A : TypeKey](
    input: Future[TraverseInput[Option[A]]],
    result: Observable[TraverseResult[A]])
  : Future[TraverseResult[Option[A]]]

  /** stages the traversal of an set's elements
   * @tparam A the type of the set elements
   * @param input the input to traversing the set
   * @return a iterator of inputs for the set's elements. an empty iterator is returned to avoid
   * traversal into the set.
   */
  protected def stageSetElements[A : TypeKey](
    input: Future[TraverseInput[Set[A]]])
  : Observable[TraverseInput[A]]

  /** unstages the traversal of an set's elements
   * @tparam A the type of the set elements
   * @param input the input to traversing the set
   * @param result an iterator of results for the set's elements. an empty iterator indicates that traversal
   * into the set has been avoided
   * @return the result of travering the set
   */
  protected def unstageSetElements[A : TypeKey](
    input: Future[TraverseInput[Set[A]]],
    result: Observable[TraverseResult[A]])
  : Future[TraverseResult[Set[A]]]

  /** stages the traversal of an list's elements
   * @tparam A the type of the list elements
   * @param input the input to traversing the list
   * @return a iterator of inputs for the list's elements. an empty iterator is returned to avoid
   * traversal into the list.
   */
  protected def stageListElements[A : TypeKey](
    input: Future[TraverseInput[List[A]]])
  : Observable[TraverseInput[A]]

  /** unstages the traversal of a list's elements
   * @tparam A the type of the list elements
   * @param input the input to traversing the list
   * @param result an iterator of results for the list's elements. an empty iterator indicates that traversal
   * into the list has been avoided
   * @return the result of travering the list
   */
  protected def unstageListElements[A : TypeKey](
    input: Future[TraverseInput[List[A]]],
    result: Observable[TraverseResult[A]])
  : Future[TraverseResult[List[A]]]

  private type TraversorFunction[A] = (Future[TraverseInput[A]]) => Future[TraverseResult[A]]

  private val basicTraversors =
    TypeKeyMap[Any, TraversorFunction] +
    traverseBoolean _ +
    traverseChar _ +
    traverseDouble _ +
    traverseFloat _ +
    traverseInt _ +
    traverseLong _ +
    traverseString _

  // custom generators have to come first. after that order is immaterial
  private def traverseAnyOption[A : TypeKey](input: Future[TraverseInput[A]]): Option[Future[TraverseResult[A]]] =
    traverseCustomOption(input) orElse
    traverseEmblemOptionFromAny(input) orElse
    traverseExtractorOption(input) orElse
    traverseOptionOption(input) orElse
    traverseSetOption(input) orElse
    traverseListOption(input) orElse
    traverseBasicOption(input)

  private def traverseCustomOption[A : TypeKey](input: Future[TraverseInput[A]])
  : Option[Future[TraverseResult[A]]] = {
    val keyOpt: Option[TypeKey[_ >: A]] = customTraversors.keys.map(_.castToLowerBound[A]).flatten.headOption
    def getCustomTraversor[B >: A : TypeKey]: CustomTraversor[B] = customTraversors(typeKey[B])
    keyOpt map { key => getCustomTraversor(key).apply[A](input) }
  }

  private def traverseEmblemOptionFromAny[A : TypeKey](input: Future[TraverseInput[A]])
  : Option[Future[TraverseResult[A]]] = {
    val keyOption = hasEmblemTypeKeyOption(typeKey[A])
    keyOption flatMap { key => introduceHasEmblemTraverseEmblemOption(input)(key) }
  }

  private def hasEmblemTypeKeyOption[A : TypeKey, B <: A with HasEmblem]: Option[TypeKey[B]] =
    if (typeKey[A].tpe <:< typeOf[HasEmblem])
      Some(typeKey[A].asInstanceOf[TypeKey[B]])
    else
      None

  private def introduceHasEmblemTraverseEmblemOption[A, B <: A with HasEmblem : TypeKey](
    input: Future[TraverseInput[A]])
  : Option[Future[TraverseResult[A]]] = {
    traverseEmblemOption[B](
      input.asInstanceOf[Future[TraverseInput[B]]]
    ).asInstanceOf[Option[Future[TraverseResult[A]]]]
  }

  private def traverseEmblemOption[A <: HasEmblem : TypeKey](input: Future[TraverseInput[A]])
  : Option[Future[TraverseResult[A]]] = {
    emblemPool.get(typeKey[A]) map { emblem => traverseFromEmblem(emblem, input) }
  }

  private def traverseFromEmblem[A <: HasEmblem](emblem: Emblem[A], hasEmblemInput: Future[TraverseInput[A]])
  : Future[TraverseResult[A]] = {
    val emblemPropInputIterator: Observable[PropInput[A, _]] =
      stageEmblemProps(emblem, hasEmblemInput)
    val emblemPropResultIterator: Observable[PropResult[A, _]] =
        emblemPropInputIterator.map { case (prop, input) =>
          (prop, traverseEmblemProp(emblem, prop, input))
        }
    unstageEmblemProps(emblem, hasEmblemInput, emblemPropResultIterator)
  }

  private def traverseEmblemProp[A <: HasEmblem, B](
    emblem: Emblem[A],
    prop: EmblemProp[A, B],
    input: Future[TraverseInput[B]])
  : Future[TraverseResult[B]] = {
    traverse(input)(prop.typeKey)
  }

  private def traverseExtractorOption[Domain : TypeKey](input: Future[TraverseInput[Domain]])
  : Option[Future[TraverseResult[Domain]]] =
    extractorPool.get[Domain] map { s => traverseFromExtractor[Domain](s, input) }

  private def traverseFromExtractor[Domain : TypeKey](
    extractor: ExtractorFor[Domain],
    input: Future[TraverseInput[Domain]])
  : Future[TraverseResult[Domain]] =
    traverseFromFullyTypedExtractor(extractor, input)

  private def traverseFromFullyTypedExtractor[Domain : TypeKey, Range](
    extractor: Extractor[Domain, Range],
    input: Future[TraverseInput[Domain]])
  : Future[TraverseResult[Domain]] = {
    val rangeInput = stageExtractor(extractor, input)
    val rangeResult = traverse(rangeInput)(extractor.rangeTypeKey)
    unstageExtractor(extractor, rangeResult)
  }

  // TODO pt-88571474: remove code duplication with option/set/list, generalize to other kinds of "collections"

  private def traverseOptionOption[OptionA : TypeKey](input: Future[TraverseInput[OptionA]])
  : Option[Future[TraverseResult[OptionA]]] = {
    val keyOption = optionElementTypeKeyOption(typeKey[OptionA])
    def doTraverse[A : TypeKey] = traverseOption(input.asInstanceOf[Future[TraverseInput[Option[A]]]])
    keyOption map { key => doTraverse(key).asInstanceOf[Future[TraverseResult[OptionA]]] }
  }

  // returns a `Some` containing the enclosing type of the option whenever the supplied type argument `A`
  // is an Option. otherwise returns `None`.
  private def optionElementTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] =
    if (typeKey[A].tpe <:< typeOf[Option[_]]) Some(typeKey[A].typeArgs.head) else None

  private[traversors] def traverseOption[A : TypeKey](optionInput: Future[TraverseInput[Option[A]]])
  : Future[TraverseResult[Option[A]]] = {
    val optionValueInputObservable: Observable[TraverseInput[A]] = stageOptionValue[A](optionInput)

    val optionValueResultObservable: Observable[TraverseResult[A]] =
      optionValueInputObservable flatMap { optionValueInput =>
        val promise = Promise.successful(optionValueInput)
        val optionValueResultFuture: Future[TraverseResult[A]] = traverse[A](promise.future)
        Observable.from(optionValueResultFuture)
      }

    unstageOptionValue[A](optionInput, optionValueResultObservable)
  }

  private def traverseSetOption[SetA : TypeKey](input: Future[TraverseInput[SetA]])
  : Option[Future[TraverseResult[SetA]]] = {
    val keyOption = setElementTypeKeyOption(typeKey[SetA])
    def doTraverse[A : TypeKey] = traverseSet(input.asInstanceOf[Future[TraverseInput[Set[A]]]])
    keyOption map { k => doTraverse(k).asInstanceOf[Future[TraverseResult[SetA]]] }
  }

  // returns a `Some` containing the enclosing type of the set whenever the supplied type argument `A`
  // is a Set. otherwise returns `None`.
  private def setElementTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] =
    if (typeKey[A].tpe <:< typeOf[Set[_]]) Some(typeKey[A].typeArgs.head) else None

  private def traverseSet[A : TypeKey](aSetInput: Future[TraverseInput[Set[A]]])
  : Future[TraverseResult[Set[A]]] = {
    val aInputObservable: Observable[TraverseInput[A]] = stageSetElements[A](aSetInput)
    val aResultObserable: Observable[TraverseResult[A]] = aInputObservable flatMap { aInput => 
      Observable.from(traverse[A](Future(aInput)))
    }
    unstageSetElements[A](aSetInput, aResultObserable)
  }

  private def traverseListOption[ListA : TypeKey](input: Future[TraverseInput[ListA]])
  : Option[Future[TraverseResult[ListA]]] = {
    val keyOption = listElementTypeKeyOption(typeKey[ListA])
    def doTraverse[A : TypeKey] = traverseList(input.asInstanceOf[Future[TraverseInput[List[A]]]])
    keyOption map { k => doTraverse(k).asInstanceOf[Future[TraverseResult[ListA]]] }
  }

  // returns a `Some` containing the enclosing type of the list whenever the supplied type argument `A`
  // is a List. otherwise returns `None`.
  private def listElementTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] =
    if (typeKey[A].tpe <:< typeOf[List[_]]) Some(typeKey[A].typeArgs.head) else None

  private def traverseList[A : TypeKey](aListInput: Future[TraverseInput[List[A]]])
  : Future[TraverseResult[List[A]]] = {
    val aInputObservable: Observable[TraverseInput[A]] = stageListElements[A](aListInput)
    val aResultObservable: Observable[TraverseResult[A]] = aInputObservable flatMap { aInput =>
      Observable.from(traverse[A](Future(aInput)))
    }
    unstageListElements[A](aListInput, aResultObservable)
  }

  private def traverseBasicOption[Basic : TypeKey](input: Future[TraverseInput[Basic]])
  : Option[Future[TraverseResult[Basic]]] =
    basicTraversors.get[Basic] map { traversor => traversor(input) }

}
