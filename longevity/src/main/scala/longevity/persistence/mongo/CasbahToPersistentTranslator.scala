package longevity.persistence.mongo

import com.github.nscala_time.time.Imports._
import com.mongodb.casbah.Imports._
import emblem.emblematic.Emblem
import emblem.emblematic.EmblemProp
import emblem.emblematic.Emblematic
import emblem.TypeKey
import emblem.emblematic.Union
import emblem.exceptions.CouldNotTraverseException
import emblem.emblematic.traversors.sync.Traversor
import emblem.typeKey
import longevity.exceptions.persistence.NotInSubdomainTranslationException
import longevity.subdomain.Embeddable
import longevity.subdomain.Persistent
import scala.reflect.runtime.universe.typeOf

/** translates
 * [[http://mongodb.github.io/casbah/api/#com.mongodb.casbah.commons.MongoDBList
 * casbah MongoDBObjects]] into [[Persistent persistent objects]].
 * 
 * expects BSON for embeddables and key values with a single property to inline
 * those embeddables.
 *
 * @param emblematic the emblematic types to use
 */
private[persistence] class CasbahToPersistentTranslator(
  private val emblematic: Emblematic) {

  /** translates a `MongoDBObject` into a [[Persistent persistent object]] */
  def translate[P <: Persistent : TypeKey](casbah: MongoDBObject): P = try {
    traversor.traverse[P](WrappedInput(casbah, true))
  } catch {
    case e: CouldNotTraverseException =>
      throw new NotInSubdomainTranslationException(typeKey[P].name, e)
  }

  private val optionAnyType = typeOf[scala.Option[_]]

  case class WrappedInput(value: Any, isUnionOrTopLevel: Boolean)

  private val traversor = new Traversor {

    type TraverseInput[A] = WrappedInput
    type TraverseResult[A] = A

    override protected val emblematic = CasbahToPersistentTranslator.this.emblematic

    override protected def traverseBoolean(input: WrappedInput): Boolean = input.value.asInstanceOf[Boolean]

    override protected def traverseChar(input: WrappedInput): Char = input.value.asInstanceOf[String](0)

    override protected def traverseDateTime(input: WrappedInput): DateTime = input.value.asInstanceOf[DateTime]

    override protected def traverseDouble(input: WrappedInput): Double = input.value.asInstanceOf[Double]

    override protected def traverseFloat(input: WrappedInput): Float = input.value.asInstanceOf[Double].toFloat

    override protected def traverseInt(input: WrappedInput): Int = input.value.asInstanceOf[Int]

    override protected def traverseLong(input: WrappedInput): Long = input.value.asInstanceOf[Long]

    override protected def traverseString(input: WrappedInput): String = input.value.asInstanceOf[String]

    override protected def constituentTypeKey[A : TypeKey](union: Union[A], input: WrappedInput)
    : TypeKey[_ <: A] = {
      val mongoDBObject: MongoDBObject = {
        val key = typeKey[A]
        if (key <:< typeOf[Persistent]) {
          input.value.asInstanceOf[MongoDBObject]
        } else if (key <:< typeOf[Embeddable]) {
          input.value.asInstanceOf[BasicDBObject]
        } else {
          throw new CouldNotTraverseException(key)
        }
      }

      val discriminator = mongoDBObject("_discriminator").asInstanceOf[String]
      union.typeKeyForName(discriminator).get
    }

    override protected def stageUnion[A : TypeKey, B <: A : TypeKey](union: Union[A], input: WrappedInput)
    : Iterable[WrappedInput] =
      Seq(input.copy(isUnionOrTopLevel = true))

    override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: WrappedInput,
      result: Iterable[B])
    : A =
      result.head

    protected def stageEmblemProps[A : TypeKey](
      emblem: Emblem[A],
      input: WrappedInput)
    : Iterable[PropInput[A, _]] = {
      if (emblem.props.size == 1 && !input.isUnionOrTopLevel) {
        Seq(emblem.props.head -> WrappedInput(input.value, false))
      } else {
        val mongoDBObject: MongoDBObject = {
          if (input.value.isInstanceOf[MongoDBObject]) {
            input.value.asInstanceOf[MongoDBObject]
          } else {
            input.value.asInstanceOf[BasicDBObject]
          }
        }

        def propInput[B](prop: EmblemProp[A, B]) = {
          if (prop.typeKey <:< optionAnyType) {
            prop -> WrappedInput(mongoDBObject.get(prop.name), false)
          }
          else {
            prop -> WrappedInput(mongoDBObject(prop.name), false)
          }
        }
        emblem.props.map(propInput(_))
      }
    }

    protected def unstageEmblemProps[A : TypeKey](
      emblem: Emblem[A],
      input: WrappedInput,
      result: Iterable[PropResult[A, _]])
    : TraverseResult[A] = {
      val builder = emblem.builder()
      result.foreach { case (prop, propResult) => builder.setProp(prop, propResult) }
      builder.build()
    }

    protected def stageOptionValue[A : TypeKey](
      input: WrappedInput)
    : Iterable[WrappedInput] =
      input.value.asInstanceOf[Option[Any]].toIterable.map(WrappedInput(_, false))

    protected def unstageOptionValue[A : TypeKey](
      input: WrappedInput,
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Option[A]] =
      result.headOption

    protected def stageSetElements[A : TypeKey](
      input: WrappedInput)
    : Iterable[WrappedInput] = {
      val list: MongoDBList = input.value.asInstanceOf[BasicDBList]
      list.map(WrappedInput(_, false))
    }

    protected def unstageSetElements[A : TypeKey](
      input: WrappedInput,
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Set[A]] =
      result.toSet

    protected def stageListElements[A : TypeKey](
      input: WrappedInput)
    : Iterable[TraverseInput[A]] = {
      val list: MongoDBList = input.value.asInstanceOf[BasicDBList]
      list.map(WrappedInput(_, false))
    }

    protected def unstageListElements[A : TypeKey](
      input: WrappedInput,
      result: Iterable[TraverseResult[A]])
    : TraverseResult[List[A]] =
      result.toList

  }

}
