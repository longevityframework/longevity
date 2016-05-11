package longevity.persistence.mongo

import com.github.nscala_time.time.Imports._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObjectBuilder
import emblem.emblematic.Emblem
import emblem.emblematic.EmblemProp
import emblem.emblematic.Emblematic
import emblem.emblematic.Extractor
import emblem.emblematic.ExtractorPool
import emblem.TypeKey
import emblem.emblematic.Union
import emblem.exceptions.CouldNotTraverseException
import emblem.exceptions.ExtractorInverseException
import emblem.emblematic.traversors.sync.Traversor
import emblem.typeKey
import longevity.exceptions.persistence.NotInSubdomainTranslationException
import longevity.exceptions.persistence.ShorthandUnabbreviationException
import longevity.persistence.RepoPool
import longevity.subdomain.Assoc
import longevity.subdomain.AssocAny
import longevity.subdomain.Entity
import longevity.subdomain.persistent.Persistent
import scala.reflect.runtime.universe.typeOf

/** translates
 * [[http://mongodb.github.io/casbah/api/#com.mongodb.casbah.commons.MongoDBList
 * casbah MongoDBObjects]] into [[Persistent persistent entities]].
 *
 * @param emblematic the emblematic types to use
 * @param repoPool a pool of the repositories for this persistence context
 */
private[persistence] class CasbahToPersistentTranslator(
  private val emblematic: Emblematic,
  private val repoPool: RepoPool) {

  /** translates a `MongoDBObject` into a [[Persistent persistent entity]] */
  def translate[P <: Persistent : TypeKey](casbah: MongoDBObject): P = try {
    traversor.traverse[P](casbah)
  } catch {
    case e: CouldNotTraverseException => throw new NotInSubdomainTranslationException(typeKey[P], e)
  }

  private val optionAnyType = typeOf[scala.Option[_]]

  private val traversor = new Traversor {

    type TraverseInput[A] = Any
    type TraverseResult[A] = A

    override protected val emblematic = CasbahToPersistentTranslator.this.emblematic
    override protected val customTraversors = CustomTraversorPool.empty + assocTraversor

    def assocTraversor = new CustomTraversor[AssocAny] {
      def apply[B <: Assoc[_ <: Persistent] : TypeKey](input: TraverseInput[B]): TraverseResult[B] = {

        // first asInstanceOf is because of emblem shortfall
        // second asInstanceOf is basically the same emblem shortfall as before
        MongoId(input.asInstanceOf[ObjectId]).asInstanceOf[B]
      }
    }

    override protected def traverseBoolean(input: Any): Boolean = input.asInstanceOf[Boolean]

    override protected def traverseChar(input: Any): Char = input.asInstanceOf[String](0)

    override protected def traverseDateTime(input: Any): DateTime = input.asInstanceOf[DateTime]

    override protected def traverseDouble(input: Any): Double = input.asInstanceOf[Double]

    override protected def traverseFloat(input: Any): Float = input.asInstanceOf[Double].toFloat

    override protected def traverseInt(input: Any): Int = input.asInstanceOf[Int]

    override protected def traverseLong(input: Any): Long = input.asInstanceOf[Long]

    override protected def traverseString(input: Any): String = input.asInstanceOf[String]

    override protected def constituentTypeKey[A : TypeKey](union: Union[A], input: Any): TypeKey[_ <: A] = {
      val mongoDBObject: MongoDBObject = {
        val key = typeKey[A]
        if (key <:< typeOf[Persistent]) {
          input.asInstanceOf[MongoDBObject]
        } else if (key <:< typeOf[Entity]) {
          input.asInstanceOf[BasicDBObject]
        } else {
          throw new CouldNotTraverseException(key)
        }
      }

      val discriminator = mongoDBObject("_discriminator").asInstanceOf[String]
      union.typeKeyForName(discriminator).get
    }

    override protected def stageUnion[A : TypeKey, B <: A : TypeKey](union: Union[A], input: Any)
    : Iterable[Any] =
      Seq(input)

    override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: Any,
      result: Iterable[B])
    : A =
      result.head

    protected def stageEmblemProps[A : TypeKey](
      emblem: Emblem[A],
      input: TraverseInput[A])
    : Iterable[PropInput[A, _]] = {
      val mongoDBObject: MongoDBObject = {
        val key = typeKey[A]
        if (key <:< typeOf[Persistent]) {
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

    protected def unstageEmblemProps[A : TypeKey](
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
