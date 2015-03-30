package emblem.traversors

import emblem._
import emblem.exceptions.CouldNotTraverseException
import emblem.reflectionUtil.makeTypeTag
import scala.reflect.runtime.universe.typeOf

/** recursively traverses a data structure by type. the inputs and the outputs of the traversal are abstract
 * here, and specified by the implementing class. this forms a generic pattern for [[Visitor visiting]],
 * [[Generator generating]], and [[Transformer transforming]] data.
 * 
 * you can traverse arbritrary data to your liking by implementing the protected vals and defs in this
 * interface. as of yet, i haven't been able to generate the scaladoc for those protected methods.
 * sorry about that.
 */
trait Traversor {

  /** the input to a traversal step over type A */
  type TraverseInput[A]

  /** the output to a traversal step over type A */
  type TraverseResult[A]

  /** a custom traversor over type A */
  trait CustomTraversor[A] {
    def apply[B <: A : TypeKey](input: TraverseInput[B]): TraverseResult[B]
  }

  /** A [[TypeKeyMap]] for [[CustomTraversor custom traversors]] */
  type CustomTraversors = TypeKeyMap[Any, CustomTraversor]

  /** An empty map of [[CustomTraversor custom traversors]] */
  val emptyCustomTraversor: CustomTraversors = TypeKeyMap[Any, CustomTraversor]()

  /** traverses an object of any supported type.
   * 
   * @throws emblem.exceptions.CouldNotTraverseException when an unsupported type is encountered during the
   * traversal
   */
  def traverse[A : TypeKey](input: TraverseInput[A]): TraverseResult[A] =
    traverseAnyOption[A](input) getOrElse {
      throw new CouldNotTraverseException(typeKey[A])
    }

  /** an input for traversing an [[EmblemProp]] */
  protected type PropInput[A <: HasEmblem, B] = (EmblemProp[A, B], TraverseInput[B])

  /** an output for traversing an [[EmblemProp]] */
  protected type PropResult[A <: HasEmblem, B] = (EmblemProp[A, B], TraverseResult[B])

  /** the emblems to use in the recursive traversal */
  protected val emblemPool: EmblemPool = EmblemPool()

  /** the extractors to use in the recursive traversal */
  protected val extractorPool: ExtractorPool = ExtractorPool()

  /** the custom traversors to use in the recursive traversal */
  protected val customTraversors: CustomTraversors = emptyCustomTraversor

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

  /** stage the traversal of a [[Emblem emblem's]] [[EmblemProp props]].
   * @param emblem the emblem being traversed
   * @param input the input to the emblem traversal
   * @return an iterator of inputs for the emblem props
   */
  protected def stageEmblemProps[A <: HasEmblem](
    emblem: Emblem[A],
    input: TraverseInput[A])
  : Iterator[PropInput[A, _]]

  /** unstage the traversal of a [[Emblem emblem's]] [[EmblemProp props]].
   * @param emblem the emblem being traversed
   * @param input the input to the emblem traversal
   * @param an iterator of the outputs for the emblem props
   * @return the output for the emblem
   */
  protected def unstageEmblemProps[A <: HasEmblem](
    emblem: Emblem[A],
    input: TraverseInput[A],
    result: Iterator[PropResult[A, _]])
  : TraverseResult[A]

  // TODO tparamas
  /** stage the traversal of a [[Extractor extractor]].
   * @param extractor the extractor being traversed
   * @param input the input to the extractor traversal
   * @return the input for traversing the [[Extractor.unapply extractor abbreviation]]
   */
  protected def stageExtractor[Domain, Range](
    extractor: Extractor[Domain, Range],
    input: TraverseInput[Range])
  : TraverseInput[Domain]

  /** unstage the traversal of a [[Extractor extractor]].
   * @param extractor the extractor being traversed
   * @param domainResult the result of traversing the [[Extractor.unapply extractor abbreviation]]
   * @return the result of traversing the extractor
   */
  protected def unstageExtractor[Domain, Range](
    extractor: Extractor[Domain, Range],
    domainResult: TraverseResult[Domain])
  : TraverseResult[Range]

  /** stage the traversal of an option's value
   * @param input the input to traversing the option
   * @return an iterator of 0 or 1 inputs of the option's value. an empty iterator is returned to avoid
   * traversal into the option. we use an option here to stand in for an iterator of 0 or 1 values. note that
   * this usage of an option is different from the kind of option we are traversing
   */
  protected def stageOptionValue[A : TypeKey](
    input: TraverseInput[Option[A]])
  : Option[TraverseInput[A]]

  /** unstage the traversal of an option's value
   * @param input the input to traversing the option
   * @param result an iterator of 0 or 1 results of the option's value. an empty iterator indicates that
   * traversal into the option has been avoided. we use an option here to stand in for an iterator of 0 or 1
   * values. note that this usage of an option is different from the kind of option we are traversing
   * @return the result of traversing the option
   */
  protected def unstageOptionValue[A : TypeKey](
    input: TraverseInput[Option[A]],
    result: Option[TraverseResult[A]])
  : TraverseResult[Option[A]]

  /** stage the traversal of an set's elements
   * @param input the input to traversing the set
   * @return a iterator of inputs for the set's elements. an empty iterator is returned to avoid
   * traversal into the set.
   */
  protected def stageSetElements[A : TypeKey](
    input: TraverseInput[Set[A]])
  : Iterator[TraverseInput[A]]

  /** unstage the traversal of an set's elements
   * @param input the input to traversing the set
   * @param result an iterator of results for the set's elements. an empty iterator indicates that traversal
   * into the set has been avoided
   * @return the result of travering the set
   */
  protected def unstageSetElements[A : TypeKey](
    input: TraverseInput[Set[A]],
    result: Iterator[TraverseResult[A]])
  : TraverseResult[Set[A]]

  /** stage the traversal of an list's elements
   * @param input the input to traversing the list
   * @return a iterator of inputs for the list's elements. an empty iterator is returned to avoid
   * traversal into the list.
   */
  protected def stageListElements[A : TypeKey](
    input: TraverseInput[List[A]])
  : Iterator[TraverseInput[A]]

  /** unstage the traversal of an list's elements
   * @param input the input to traversing the list
   * @param result an iterator of results for the list's elements. an empty iterator indicates that traversal
   * into the list has been avoided
   * @return the result of travering the list
   */
  protected def unstageListElements[A : TypeKey](
    input: TraverseInput[List[A]],
    result: Iterator[TraverseResult[A]])
  : TraverseResult[List[A]]

  private type TraversorFunction[A] = (TraverseInput[A]) => TraverseResult[A]

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
  private def traverseAnyOption[A : TypeKey](input: TraverseInput[A]): Option[TraverseResult[A]] =
    traverseCustomOption(input) orElse
    traverseEmblemOptionFromAny(input) orElse
    traverseExtractorOption(input) orElse
    traverseOptionOption(input) orElse
    traverseSetOption(input) orElse
    traverseListOption(input) orElse
    traverseBasicOption(input)

  private def traverseCustomOption[A : TypeKey](input: TraverseInput[A]): Option[TraverseResult[A]] = {
    val keyOpt: Option[TypeKey[_ >: A]] = customTraversors.keys.map(_.castToLowerBound[A]).flatten.headOption
    def getCustomTraversor[B >: A : TypeKey]: CustomTraversor[B] = customTraversors(typeKey[B])
    keyOpt map { key => getCustomTraversor(key).apply[A](input) }
  }

  private def traverseEmblemOptionFromAny[A : TypeKey](input: TraverseInput[A]): Option[TraverseResult[A]] = {
    val keyOption = hasEmblemTypeKeyOption(typeKey[A])
    keyOption flatMap { key => introduceHasEmblemTraverseEmblemOption(input)(key) }
  }

  private def hasEmblemTypeKeyOption[A : TypeKey, B <: A with HasEmblem]: Option[TypeKey[B]] =
    if (typeKey[A].tpe <:< typeOf[HasEmblem])
      Some(typeKey[A].asInstanceOf[TypeKey[B]])
    else
      None

  private def introduceHasEmblemTraverseEmblemOption[A, B <: A with HasEmblem : TypeKey](input: TraverseInput[A])
  : Option[TraverseResult[A]] = {
    traverseEmblemOption[B](input.asInstanceOf[TraverseInput[B]]).asInstanceOf[Option[TraverseResult[A]]]
  }

  private def traverseEmblemOption[A <: HasEmblem : TypeKey](input: TraverseInput[A])
  : Option[TraverseResult[A]] = {
    emblemPool.get(typeKey[A]) map { emblem => traverseFromEmblem(emblem, input) }
  }

  private def traverseFromEmblem[A <: HasEmblem](emblem: Emblem[A], hasEmblemInput: TraverseInput[A])
  : TraverseResult[A] = {
    val emblemPropInputIterator: Iterator[PropInput[A, _]] =
      stageEmblemProps(emblem, hasEmblemInput)
    val emblemPropResultIterator: Iterator[PropResult[A, _]] =
        emblemPropInputIterator.map { case (prop, input) =>
          (prop, traverseEmblemProp(emblem, prop, input))
        }
    unstageEmblemProps(emblem, hasEmblemInput, emblemPropResultIterator)
  }

  private def traverseEmblemProp[A <: HasEmblem, B](
    emblem: Emblem[A],
    prop: EmblemProp[A, B],
    input: TraverseInput[B])
  : TraverseResult[B] = {
    traverse(input)(prop.typeKey)
  }

  private def traverseExtractorOption[Range : TypeKey](input: TraverseInput[Range])
  : Option[TraverseResult[Range]] =
    extractorPool.get[Range] map { s => traverseFromExtractor[Range](s, input) }

  private def traverseFromExtractor[Range](
    extractor: Extractor[_, Range],
    input: TraverseInput[Range])
  : TraverseResult[Range] =
    traverseFromFullyTypedExtractor(extractor, input)

  private def traverseFromFullyTypedExtractor[Domain, Range](
    extractor: Extractor[Domain, Range],
    input: TraverseInput[Range])
  : TraverseResult[Range] = {
    val domainInput = stageExtractor(extractor, input)
    val domainResult = traverse(domainInput)(extractor.domainTypeKey)
    unstageExtractor(extractor, domainResult)
  }

  // TODO pt 88571474: remove code duplication with option/set/list, generalize to other kinds of "collections"

  private def traverseOptionOption[OptionA : TypeKey](input: TraverseInput[OptionA])
  : Option[TraverseResult[OptionA]] = {
    val keyOption = optionElementTypeKeyOption(typeKey[OptionA])
    def doTraverse[A : TypeKey] = traverseOption(input.asInstanceOf[TraverseInput[Option[A]]])
    keyOption map { key => doTraverse(key).asInstanceOf[TraverseResult[OptionA]] }
  }

  // returns a `Some` containing the enclosing type of the option whenever the supplied type argument `A`
  // is an Option. otherwise returns `None`.
  private def optionElementTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] =
    if (typeKey[A].tpe <:< typeOf[Option[_]]) Some(typeKey[A].typeArgs.head) else None

  private def traverseOption[A : TypeKey](optionInput: TraverseInput[Option[A]]): TraverseResult[Option[A]] = {
    val optionValueInputOption: Option[TraverseInput[A]] = stageOptionValue[A](optionInput)
    val optionValueResultOption: Option[TraverseResult[A]] = optionValueInputOption map { optionValueInput =>
      traverse[A](optionValueInput)
    }
    unstageOptionValue[A](optionInput, optionValueResultOption)
  }

  private def traverseSetOption[SetA : TypeKey](input: TraverseInput[SetA]): Option[TraverseResult[SetA]] = {
    val keyOption = setElementTypeKeyOption(typeKey[SetA])
    def doTraverse[A : TypeKey] = traverseSet(input.asInstanceOf[TraverseInput[Set[A]]])
    keyOption map { k => doTraverse(k).asInstanceOf[TraverseResult[SetA]] }
  }

  // returns a `Some` containing the enclosing type of the set whenever the supplied type argument `A`
  // is a Set. otherwise returns `None`.
  private def setElementTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] =
    if (typeKey[A].tpe <:< typeOf[Set[_]]) Some(typeKey[A].typeArgs.head) else None

  private def traverseSet[A : TypeKey](aSetInput: TraverseInput[Set[A]]): TraverseResult[Set[A]] = {
    val aInputIterator: Iterator[TraverseInput[A]] = stageSetElements[A](aSetInput)
    val aResultIterator: Iterator[TraverseResult[A]] = aInputIterator map { aInput => traverse[A](aInput) }
    unstageSetElements[A](aSetInput, aResultIterator)
  }

  private def traverseListOption[ListA : TypeKey](input: TraverseInput[ListA]): Option[TraverseResult[ListA]] = {
    val keyOption = listElementTypeKeyOption(typeKey[ListA])
    def doTraverse[A : TypeKey] = traverseList(input.asInstanceOf[TraverseInput[List[A]]])
    keyOption map { k => doTraverse(k).asInstanceOf[TraverseResult[ListA]] }
  }

  private def traverseList[A : TypeKey](aListInput: TraverseInput[List[A]]): TraverseResult[List[A]] = {
    val aInputIterator: Iterator[TraverseInput[A]] = stageListElements[A](aListInput)
    val aResultIterator: Iterator[TraverseResult[A]] = aInputIterator map { aInput => traverse[A](aInput) }
    unstageListElements[A](aListInput, aResultIterator)
  }

  // returns a `Some` containing the enclosing type of the list whenever the supplied type argument `A`
  // is a List. otherwise returns `None`.
  private def listElementTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] =
    if (typeKey[A].tpe <:< typeOf[List[_]]) Some(typeKey[A].typeArgs.head) else None

  private def traverseBasicOption[Basic : TypeKey](input: TraverseInput[Basic]): Option[TraverseResult[Basic]] =
    basicTraversors.get[Basic] map { traversor => traversor(input) }

}
