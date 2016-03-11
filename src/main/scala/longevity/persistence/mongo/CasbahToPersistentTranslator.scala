package longevity.persistence.mongo

import com.github.nscala_time.time.Imports._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObjectBuilder
import emblem.imports._
import emblem.exceptions.CouldNotTraverseException
import emblem.exceptions.ExtractorInverseException
import emblem.traversors.sync.Traversor
import longevity.exceptions.persistence.BsonTranslationException
import longevity.exceptions.persistence.ShorthandUnabbreviationException
import longevity.persistence.RepoPool
import longevity.subdomain._
import scala.reflect.runtime.universe.typeOf

/** translates
 * [[http://mongodb.github.io/casbah/api/#com.mongodb.casbah.commons.MongoDBList
 * casbah MongoDBObjects]] into [[Persistent persistent entities]].
 *
 * @param emblemPool a pool of emblems for the entities within the subdomain
 * @param extractorPool a complete set of the extractors used by the bounded context
 * @param repoPool a pool of the repositories for this persistence context
 */
private[persistence] class CasbahToPersistentTranslator(
  emblemPool: EmblemPool,
  extractorPool: ExtractorPool,
  private val repoPool: RepoPool) {

  /** translates a `MongoDBObject` into a [[Persistent persistent entity]] */
  def translate[P <: Persistent : TypeKey](casbah: MongoDBObject): P = try {
    traversor.traverse[P](casbah)
  } catch {
    case e: CouldNotTraverseException => throw new BsonTranslationException(typeKey[P], e)
  }

  private val optionAnyType = typeOf[scala.Option[_]]

  private val traversor = new Traversor {

    type TraverseInput[A] = Any
    type TraverseResult[A] = A

    override protected val emblemPool = CasbahToPersistentTranslator.this.emblemPool
    override protected val extractorPool = CasbahToPersistentTranslator.this.extractorPool
    override protected val customTraversors = CustomTraversorPool.empty + assocTraversor

    def assocTraversor = new CustomTraversor[AssocAny] {
      def apply[B <: Assoc[_ <: Persistent] : TypeKey](input: TraverseInput[B]): TraverseResult[B] = {

        // first asInstanceOf is because of emblem shortfall
        // second asInstanceOf is basically the same emblem shortfall as before
        MongoId(input.asInstanceOf[ObjectId]).asInstanceOf[B]
      }
    }

    protected def traverseBoolean(input: TraverseInput[Boolean]): TraverseResult[Boolean] =
      input.asInstanceOf[Boolean]

    protected def traverseChar(input: TraverseInput[Char]): TraverseResult[Char] =
      input.asInstanceOf[String](0)

    protected def traverseDateTime(input: TraverseInput[DateTime]): TraverseResult[DateTime] =
      input.asInstanceOf[DateTime]

    protected def traverseDouble(input: TraverseInput[Double]): TraverseResult[Double] =
      input.asInstanceOf[Double]

    protected def traverseFloat(input: TraverseInput[Float]): TraverseResult[Float] =
      input.asInstanceOf[Double].toFloat

    protected def traverseInt(input: TraverseInput[Int]): TraverseResult[Int] =
      input.asInstanceOf[Int]

    protected def traverseLong(input: TraverseInput[Long]): TraverseResult[Long] =
      input.asInstanceOf[Long]

    protected def traverseString(input: TraverseInput[String]): TraverseResult[String] =
      input.asInstanceOf[String]

    protected def stageEmblemProps[A <: HasEmblem : TypeKey](
      emblem: Emblem[A],
      input: TraverseInput[A])
    : Iterable[PropInput[A, _]] = {
      val mongoDBObject: MongoDBObject = {
        val key = typeKey[A]
        if (key <:< typeOf[Root]) {
          input.asInstanceOf[MongoDBObject]
        } else if (key <:< typeOf[Entity]) {
          input.asInstanceOf[BasicDBObject]
        } else {
          throw new CouldNotTraverseException(key)
        }
      }

      def propInput[B](prop: EmblemProp[A, B]) = {
        if (prop.typeKey <:< optionAnyType) {
          prop -> mongoDBObject.get(prop.name)
        }
        else {
          prop -> mongoDBObject(prop.name)
        }
      }
      emblem.props.map(propInput(_))
    }

    protected def unstageEmblemProps[A <: HasEmblem : TypeKey](
      emblem: Emblem[A],
      result: Iterable[PropResult[A, _]])
    : TraverseResult[A] = {
      val builder = emblem.builder()
      result.foreach { case (prop, propResult) => builder.setProp(prop, propResult) }
      builder.build()
    }

    protected def stageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      input: TraverseInput[Domain])
    : TraverseInput[Range] =
      input

    protected def unstageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      rangeResult: TraverseResult[Range])
    : TraverseResult[Domain] =
      try {
        extractor.inverse(rangeResult)
      } catch {
        case e: ExtractorInverseException =>
          throw new ShorthandUnabbreviationException(rangeResult, typeKey[Domain], e)
      }

    protected def stageOptionValue[A : TypeKey](
      input: TraverseInput[Option[A]])
    : Iterable[TraverseInput[A]] =
      input.asInstanceOf[Option[TraverseInput[A]]].toIterable

    protected def unstageOptionValue[A : TypeKey](
      input: TraverseInput[Option[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Option[A]] =
      result.headOption

    protected def stageSetElements[A : TypeKey](
      input: TraverseInput[Set[A]])
    : Iterable[TraverseInput[A]] = {
      val list: MongoDBList = input.asInstanceOf[BasicDBList]
      list
    }

    protected def unstageSetElements[A : TypeKey](
      input: TraverseInput[Set[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Set[A]] =
      result.toSet

    protected def stageListElements[A : TypeKey](
      input: TraverseInput[List[A]])
    : Iterable[TraverseInput[A]] = {
      val list: MongoDBList = input.asInstanceOf[BasicDBList]
      list
    }

    protected def unstageListElements[A : TypeKey](
      input: TraverseInput[List[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[List[A]] =
      result.toList

  }

}
