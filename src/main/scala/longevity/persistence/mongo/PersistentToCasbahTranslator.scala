package longevity.persistence.mongo

import com.github.nscala_time.time.Imports._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObjectBuilder
import emblem.Emblem
import emblem.EmblemProp
import emblem.Emblematic
import emblem.Extractor
import emblem.HasEmblem
import emblem.TypeKey
import emblem.Union
import emblem.exceptions.CouldNotTraverseException
import emblem.traversors.sync.Traversor
import emblem.typeKey
import longevity.exceptions.persistence.AssocIsUnpersistedException
import longevity.exceptions.persistence.BsonTranslationException
import longevity.persistence.RepoPool
import longevity.subdomain.Assoc
import longevity.subdomain.AssocAny
import longevity.subdomain.Entity
import longevity.subdomain.persistent.Persistent
import scala.reflect.runtime.universe.typeOf

/** translates [[Persistent persistent entities]] into
 * [[http://mongodb.github.io/casbah/api/#com.mongodb.casbah.commons.MongoDBObject
 * casbah MongoDBObjects]].
 *
 * @param emblematic the emblematic types to use
 * @param repoPool a pool of the repositories for this persistence context
 */
private[persistence] class PersistentToCasbahTranslator(
  private val emblematic: Emblematic,
  private val repoPool: RepoPool) {

  /** translates an [[Entity]] into a `MongoDBObjects` */
  def translate[E <: Entity : TypeKey](e: E): MongoDBObject = try {
    traversor.traverse[E](e).asInstanceOf[BasicDBObject]
  } catch {
    case e: CouldNotTraverseException => throw new BsonTranslationException(e.typeKey, e)
  }

  private val optionAnyType = typeOf[scala.Option[_]]

  private val traversor = new Traversor {

    type TraverseInput[A] = A
    type TraverseResult[A] = Any

    override protected val emblematic = PersistentToCasbahTranslator.this.emblematic
    override protected val customTraversors = CustomTraversorPool.empty + assocTraversor

    def assocTraversor = new CustomTraversor[AssocAny] {
      def apply[B <: Assoc[_ <: Persistent] : TypeKey](input: TraverseInput[B]): TraverseResult[B] = {
        if (!input.isPersisted) {
          throw new AssocIsUnpersistedException(input)
        }
        input.asInstanceOf[MongoId[_ <: Persistent]].objectId
      }
    }

    override protected def traverseBoolean(input: Boolean): Any = input

    override protected def traverseChar(input: Char): Any = input

    override protected def traverseDateTime(input: DateTime): Any = input

    override protected def traverseDouble(input: Double): Any = input

    override protected def traverseFloat(input: Float): Any = input

    override protected def traverseInt(input: Int): Any = input

    override protected def traverseLong(input: Long): Any = input

    override protected def traverseString(input: String): Any = input

    override protected def constituentTypeKey[A : TypeKey](
      union: Union[A],
      input: A)
    : TypeKey[_ <: A] =
      union.typeKeyForInstance(input).get

    override protected def stageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: A)
    : Iterable[B] =
      Seq(input.asInstanceOf[B])

    override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: A,
      result: Iterable[Any])
    : Any =
      result.head.asInstanceOf[MongoDBObject] + ("_discriminator" -> typeKey[B].name)

    override protected def stageEmblemProps[A <: HasEmblem : TypeKey](
      emblem: Emblem[A],
      input: A)
    : Iterable[PropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) = prop -> prop.get(input)
      emblem.props.map(propInput(_))
    }

    override protected def unstageEmblemProps[A <: HasEmblem : TypeKey](
      emblem: Emblem[A],
      result: Iterable[PropResult[A, _]])
    : TraverseResult[A] = {
      val builder = new MongoDBObjectBuilder()
      result.foreach {
        case (prop, propResult) =>
          if (!(prop.typeKey <:< optionAnyType) || propResult != None) {
            builder += prop.name -> propResult
          }
      }
      builder.result()
    }

    override protected def stageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      input: TraverseInput[Domain])
    : TraverseInput[Range] =
      extractor.apply(input)

    override protected def unstageExtractor[Domain : TypeKey, Range : TypeKey](
      extractor: Extractor[Domain, Range],
      rangeResult: TraverseResult[Range])
    : TraverseResult[Domain] =
      rangeResult

    override protected def stageOptionValue[A : TypeKey](
      input: TraverseInput[Option[A]])
    : Iterable[TraverseInput[A]] =
      input.toIterable

    override protected def unstageOptionValue[A : TypeKey](
      input: TraverseInput[Option[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Option[A]] =
      result.headOption

    override protected def stageSetElements[A : TypeKey](
      input: TraverseInput[Set[A]])
    : Iterable[TraverseInput[A]] =
      input

    override protected def unstageSetElements[A : TypeKey](
      input: TraverseInput[Set[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Set[A]] =
      result.toSet

    override protected def stageListElements[A : TypeKey](
      input: TraverseInput[List[A]])
    : Iterable[TraverseInput[A]] =
      input

    override protected def unstageListElements[A : TypeKey](
      input: TraverseInput[List[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[List[A]] =
      result.toList

  }

}
