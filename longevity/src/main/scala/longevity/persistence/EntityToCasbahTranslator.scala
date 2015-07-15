package longevity.persistence

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObjectBuilder
import emblem.imports._
import emblem.exceptions.CouldNotTraverseException
import emblem.traversors.sync.Traversor
import longevity.exceptions.CouldNotTranslateException
import longevity.subdomain._

/** translates [[Entity entities]] into
 * [[http://mongodb.github.io/casbah/api/#com.mongodb.casbah.commons.MongoDBList casbah MongoDBObjects]].
 *
 * @param emblemPool a pool of emblems for the entities within the subdomain
 * @param extractorPool a complete set of the extractors used by the bounded context
 * @param repoPool a pool of the repositories for this persistence context
 */
private[persistence] class EntityToCasbahTranslator(
  emblemPool: EmblemPool,
  extractorPool: ExtractorPool,
  private val repoPool: RepoPool) {

  /** translates an [[Entity]] into a `MongoDBList` */
  def translate[E <: Entity : TypeKey](e: E): MongoDBObject = try {
    traversor.traverse[E](e).asInstanceOf[BasicDBObject]
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotTranslateException(e.typeKey, e)
  }

  private val traversor = new Traversor {

    type TraverseInput[A] = A
    type TraverseResult[A] = Any

    override protected val emblemPool: EmblemPool = EntityToCasbahTranslator.this.emblemPool
    override protected val extractorPool: ExtractorPool = EntityToCasbahTranslator.this.extractorPool
    override protected val customTraversors: CustomTraversorPool = CustomTraversorPool.empty + assocTraversor

    def assocTraversor = new CustomTraversor[AssocAny] {
      def apply[B <: Assoc[_ <: RootEntity] : TypeKey](input: TraverseInput[B]): TraverseResult[B] = {
        val associateeTypeKey = typeKey[B].typeArgs(0).asInstanceOf[TypeKey[_ <: RootEntity]]
 
        // TODO pt-91220826: get rid of asInstanceOf by tightening type on repo pools and repo layers
        val associateeRepo = repoPool(associateeTypeKey).asInstanceOf[MongoRepo[_]]

        input.asInstanceOf[associateeRepo.MongoId].objectId
      }
    }

    protected def traverseBoolean(input: TraverseInput[Boolean]): TraverseResult[Boolean] = input

    protected def traverseChar(input: TraverseInput[Char]): TraverseResult[Char] = input

    protected def traverseDouble(input: TraverseInput[Double]): TraverseResult[Double] = input

    protected def traverseFloat(input: TraverseInput[Float]): TraverseResult[Float] = input

    protected def traverseInt(input: TraverseInput[Int]): TraverseResult[Int] = input

    protected def traverseLong(input: TraverseInput[Long]): TraverseResult[Long] = input

    protected def traverseString(input: TraverseInput[String]): TraverseResult[String] = input

    protected def stageEmblemProps[A <: HasEmblem](
      emblem: Emblem[A],
      input: TraverseInput[A])
    : Iterable[PropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) = prop -> prop.get(input)
      emblem.props.map(propInput(_))
    }

    protected def unstageEmblemProps[A <: HasEmblem](emblem: Emblem[A], result: Iterable[PropResult[A, _]])
    : TraverseResult[A] = {
      val builder = new MongoDBObjectBuilder()
      result.foreach {
        case (prop, propResult) => builder += prop.name -> propResult
      }
      builder.result()
    }

    protected def stageExtractor[Domain : TypeKey, Range](
      extractor: Extractor[Domain, Range],
      input: TraverseInput[Domain])
    : TraverseInput[Range] =
      extractor.apply(input)

    protected def unstageExtractor[Domain : TypeKey, Range](
      extractor: Extractor[Domain, Range],
      rangeResult: TraverseResult[Range])
    : TraverseResult[Domain] =
      rangeResult

    protected def stageOptionValue[A : TypeKey](
      input: TraverseInput[Option[A]])
    : Iterable[TraverseInput[A]] =
      input.toIterable

    protected def unstageOptionValue[A : TypeKey](
      input: TraverseInput[Option[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Option[A]] =
      result.headOption

    protected def stageSetElements[A : TypeKey](
      input: TraverseInput[Set[A]])
    : Iterable[TraverseInput[A]] =
      input

    protected def unstageSetElements[A : TypeKey](
      input: TraverseInput[Set[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Set[A]] =
      result.toSet

    protected def stageListElements[A : TypeKey](
      input: TraverseInput[List[A]])
    : Iterable[TraverseInput[A]] =
      input

    protected def unstageListElements[A : TypeKey](
      input: TraverseInput[List[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[List[A]] =
      result.toList

  }

}
