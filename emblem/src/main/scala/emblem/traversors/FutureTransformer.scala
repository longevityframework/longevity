package emblem.traversors

import emblem.TypeBoundFunction
import emblem.exceptions.CouldNotTransformException
import emblem.exceptions.CouldNotTraverseException
import emblem.exceptions.ExtractorInverseException
import emblem.imports._
import emblem.traversors.FutureTransformer._
import rx.lang.scala.Observable
import rx.lang.scala.Subscription
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.util.Failure
import scala.util.Success

// TODO update comments

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
trait FutureTransformer {

  /** transforms an element of type `A`
   * @throws emblem.exceptions.CouldNotTransformException when it encounters a type it doesn't know how to
   * transform
   */
  def transform[A : TypeKey](input: Future[A]): Future[A] = try {
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
  protected def transformBoolean(input: Future[Boolean]): Future[Boolean] = input

  /** transforms a char */
  protected def transformChar(input: Future[Char]): Future[Char] = input

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

  private lazy val traversor = new FutureTraversor {

    type TraverseInput[A] = A
    type TraverseResult[A] = A

    def traverseBoolean(input: Future[Boolean]): Future[Boolean] = transformBoolean(input)

    def traverseChar(input: Future[Char]): Future[Char] = transformChar(input)

    def traverseDouble(input: Future[Double]): Future[Double] = transformDouble(input)

    def traverseFloat(input: Future[Float]): Future[Float] = transformFloat(input)

    def traverseInt(input: Future[Int]): Future[Int] = transformInt(input)

    def traverseLong(input: Future[Long]): Future[Long] = transformLong(input)

    def traverseString(input: Future[String]): Future[String] = transformString(input)

    override protected val extractorPool = FutureTransformer.this.extractorPool
    override protected val emblemPool = FutureTransformer.this.emblemPool

    override protected val customTraversors = {
      class VisCustomTraversor[A](val customTransformer: CustomTransformer[A]) extends CustomTraversor[A] {
        def apply[B <: A : TypeKey](input: Future[B]): Future[B] =
          customTransformer.apply[B](FutureTransformer.this, input)
      }
      val transformerToTraversor = new TypeBoundFunction[Any, CustomTransformer, CustomTraversor] {
        def apply[A](transformer: CustomTransformer[A]): CustomTraversor[A] = new VisCustomTraversor(transformer)
      }
      customTransformers.mapValues(transformerToTraversor)
    }

    protected def stageEmblemProps[A <: HasEmblem](emblem: Emblem[A], input: Future[A])
    : Observable[PropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) = (prop, input map { i => prop.get(i) })
      Observable.from(emblem.props.map(propInput(_)))
    }

    protected def unstageEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      input: Future[A],
      result: Observable[PropResult[A, _]])
    : Future[A] = {

      // TODO figure out how to do this without locking

      val promise = Promise[A]
      val builder = emblem.builder()

      val lock = new AnyRef
      var resultCompleted = false
      var unfinishedProps = emblem.props.toSet
      def completeUnstage(): Unit =
        if (resultCompleted && unfinishedProps.isEmpty) promise.success(builder.build())

      result.subscribe(
        { case (prop, propResultFuture) =>
          lock.synchronized { unfinishedProps += prop }
          propResultFuture.onSuccess {
            case propResult =>
              lock.synchronized {
                builder.setProp(prop, propResult)
                unfinishedProps -= prop
                completeUnstage()
              }
          }
          propResultFuture.onFailure { case e => promise.failure(e) }
        },
        { (e) => promise.failure(e) },
        { () =>
          lock.synchronized {
            resultCompleted = true
            completeUnstage()
          }
        }
      )

      promise.future
    }

    protected def stageExtractor[Domain : TypeKey, Range](
      extractor: Extractor[Domain, Range],
      domain: Future[Domain])
    : Future[Range] =
      domain map extractor.apply

    protected def unstageExtractor[Domain : TypeKey, Range](
      extractor: Extractor[Domain, Range],
      range: Future[Range])
    : Future[Domain] =
      try {
        range map extractor.inverse
      } catch {
        case e: Exception => throw new ExtractorInverseException(range, typeKey[Domain], e)
      }

    protected def stageOptionValue[A : TypeKey](input: Future[Option[A]]): Observable[A] =
      Observable.create { observer =>
        input.onComplete { tryOptionA =>
          tryOptionA match {
            case Success(optionA) =>
              optionA foreach { a => observer.onNext(a) }
              observer.onCompleted()
            case Failure(e) =>
              observer.onError(e)
          }
        }
        Subscription()
      }

    protected def unstageOptionValue[A : TypeKey](input: Future[Option[A]], result: Observable[A])
    : Future[Option[A]] = {
      val promise = Promise[Option[A]]()
      var optionA: Option[A] = None
      result subscribe(
        { (a) => optionA = Some(a) },
        { (e) => promise.failure(e) },
        { () => promise.success(optionA) }
      )
      promise.future
    }

    protected def stageSetElements[A : TypeKey](input: Future[Set[A]]): Observable[A] = {
      Observable.create { observer =>
        input.onComplete { trySetA =>
          trySetA match {
            case Success(setA) =>
              setA foreach { a => observer.onNext(a) }
              observer.onCompleted()
            case Failure(e) =>
              observer.onError(e)
          }
        }
        Subscription()
      }
    }

    protected def unstageSetElements[A : TypeKey](input: Future[Set[A]], result: Observable[A])
    : Future[Set[A]] = {
      val promise = Promise[Set[A]]()
      var setA = Set[A]()
      result subscribe(
        { (a) => setA += a },
        { (e) => promise.failure(e) },
        { () => promise.success(setA) }
      )
      promise.future
    }

    protected def stageListElements[A : TypeKey](input: Future[List[A]]): Observable[A] = {
      Observable.create { observer =>
        input.onComplete { tryListA =>
          tryListA match {
            case Success(listA) =>
              listA foreach { a => observer.onNext(a) }
              observer.onCompleted()
            case Failure(e) =>
              observer.onError(e)
          }
        }
        Subscription()
      }
    }

    protected def unstageListElements[A : TypeKey](input: Future[List[A]], result: Observable[A])
    : Future[List[A]] = {
      val promise = Promise[List[A]]()
      var listA = List[A]()
      result subscribe(
        { (a) => listA +:= a },
        { (e) => promise.failure(e) },
        { () => promise.success(listA) }
      )
      promise.future
    }

  }

}

/** holds types and zero values used by the [[Transformer transformers]], and supplies the API for custom
 * tranformers
 */
object FutureTransformer {

  /** a custom transformer of things of type A */
  trait CustomTransformer[A] {

    /** transforms an element of type `B`
     * @tparam B the type of element to transform. a subtype of `A`
     * @param transformer the [[Transformer]] that is delegating this call to us
     * @param input the element to transform
     */
    def apply[B <: A : TypeKey](transformer: FutureTransformer, input: Future[B]): Future[B]

  }

  /** a [[TypeKeyMap]] for [[CustomTransformer transformer functions]] */
  type CustomTransformerPool = TypeKeyMap[Any, CustomTransformer]

  object CustomTransformerPool {

    /** an empty map of [[CustomTransformer transformer functions]] */
    def empty: CustomTransformerPool = TypeKeyMap[Any, CustomTransformer]()
  }

}
