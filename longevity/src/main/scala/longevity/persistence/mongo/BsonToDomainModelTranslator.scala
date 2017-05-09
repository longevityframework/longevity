package longevity.persistence.mongo

import emblem.TypeKey
import emblem.emblematic.Emblem
import emblem.emblematic.EmblemProp
import emblem.emblematic.Emblematic
import emblem.emblematic.Union
import emblem.emblematic.traversors.sync.Traversor
import emblem.exceptions.CouldNotTraverseException
import emblem.typeKey
import longevity.exceptions.persistence.NotInDomainModelTranslationException
import org.bson.BsonDocument
import org.bson.BsonNull
import org.bson.BsonValue
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.reflect.runtime.universe.typeOf

/** translates [[http://mongodb.github.io/mongo-java-driver/3.2/bson/documents/
 * BSON]] into [[longevity.model.ModelType domainModel elements]] such as
 * [[Persistent persistent objects]].
 * 
 * expects BSON for embeddables and key values with a single property to inline
 * those embeddables.
 *
 * @param emblematic the emblematic types to use
 */
private[persistence] class BsonToDomainModelTranslator(
  private val emblematic: Emblematic) {

  /** translates a `MongoDBObject` into a [[Persistent persistent object]] */
  def translate[P : TypeKey](bson: BsonDocument): P = try {
    traversor.traverse[P](WrappedInput(bson, true))
  } catch {
    case e: CouldNotTraverseException =>
      throw new NotInDomainModelTranslationException(typeKey[P].name, e)
  }

  private val optionAnyType = typeOf[scala.Option[_]]

  case class WrappedInput(value: BsonValue, isUnionOrTopLevel: Boolean)

  private val traversor = new Traversor {

    type TraverseInput[A] = WrappedInput
    type TraverseResult[A] = A

    override protected val emblematic = BsonToDomainModelTranslator.this.emblematic

    override protected def traverseBoolean(input: WrappedInput): Boolean = input.value.asBoolean.getValue

    override protected def traverseChar(input: WrappedInput): Char = input.value.asString.getValue.apply(0)

    override protected def traverseDateTime(input: WrappedInput): DateTime =
      new DateTime(input.value.asDateTime.getValue, DateTimeZone.UTC)

    override protected def traverseDouble(input: WrappedInput): Double = input.value.asDouble.getValue

    override protected def traverseFloat(input: WrappedInput): Float = input.value.asDouble.getValue.toFloat

    override protected def traverseInt(input: WrappedInput): Int = input.value.asInt32.getValue

    override protected def traverseLong(input: WrappedInput): Long = input.value.asInt64.getValue

    override protected def traverseString(input: WrappedInput): String = input.value.asString.getValue

    override protected def constituentTypeKey[A : TypeKey](union: Union[A], input: WrappedInput)
    : TypeKey[_ <: A] = {
      val document = input.value.asDocument
      val discriminator = document.getString("_discriminator").getValue
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
        val document = input.value.asDocument
        def propInput[B](prop: EmblemProp[A, B]) = {
          if (prop.typeKey <:< optionAnyType) {
            if (! document.containsKey(prop.name)) {
              prop -> WrappedInput(BsonNull.VALUE, false)
            } else {
              prop -> WrappedInput(document.get(prop.name), false)
            }
          }
          else {
            prop -> WrappedInput(document.get(prop.name), false)
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
      input.value match {
        case BsonNull.VALUE => Seq()
        case _ => Seq(input.copy(isUnionOrTopLevel = false))
      }

    protected def unstageOptionValue[A : TypeKey](
      input: WrappedInput,
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Option[A]] =
      result.headOption

    protected def stageSetElements[A : TypeKey](
      input: WrappedInput)
    : Iterable[WrappedInput] = {
      input.value.asArray.asScala.map(WrappedInput(_, false))
    }

    protected def unstageSetElements[A : TypeKey](
      input: WrappedInput,
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Set[A]] =
      result.toSet

    protected def stageListElements[A : TypeKey](
      input: WrappedInput)
    : Iterable[TraverseInput[A]] = {
      input.value.asArray.asScala.map(WrappedInput(_, false))
    }

    protected def unstageListElements[A : TypeKey](
      input: WrappedInput,
      result: Iterable[TraverseResult[A]])
    : TraverseResult[List[A]] =
      result.toList

  }

}
