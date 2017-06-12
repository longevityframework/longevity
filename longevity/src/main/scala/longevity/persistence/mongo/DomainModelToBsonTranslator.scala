package longevity.persistence.mongo

import typekey.TypeKey
import longevity.emblem.emblematic.Emblem
import longevity.emblem.emblematic.EmblemProp
import longevity.emblem.emblematic.Emblematic
import longevity.emblem.emblematic.Union
import longevity.emblem.emblematic.traversors.sync.Traversor
import longevity.emblem.exceptions.CouldNotTraverseException
import typekey.typeKey
import longevity.exceptions.persistence.NotInDomainModelTranslationException
import org.bson.BsonArray
import org.bson.BsonBoolean
import org.bson.BsonDateTime
import org.bson.BsonDocument
import org.bson.BsonDouble
import org.bson.BsonInt32
import org.bson.BsonInt64
import org.bson.BsonNull
import org.bson.BsonString
import org.bson.BsonValue
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.reflect.runtime.universe.typeOf

/** translates [[longevity.model.ModelType model type elements]] such as
 * [[Persistent persistent objects]] into
 * [[http://mongodb.github.io/mongo-java-driver/3.2/bson/documents/ BSON]].
 * 
 * embeddables and key values with a single property will be inlined in the BSON.
 *
 * @param emblematic the emblematic types to use
 */
private[persistence] class DomainModelToBsonTranslator(
  private val emblematic: Emblematic) {

  /** translates a model type element into BSON */
  def translate[A : TypeKey](a: A, isUnionOrTopLevel: Boolean): BsonValue = try {
    traversor.traverse[A](WrappedInput(a, isUnionOrTopLevel))
  } catch {
    case e: CouldNotTraverseException =>
      throw new NotInDomainModelTranslationException(e.typeKey.name, e)
  }

  private val optionAnyType = typeOf[scala.Option[_]]

  case class WrappedInput[A](value: A, isUnionOrTopLevel: Boolean)

  private val traversor = new Traversor {

    type TraverseInput[A] = WrappedInput[A]
    type TraverseResult[A] = BsonValue

    override protected val emblematic = DomainModelToBsonTranslator.this.emblematic

    override protected def traverseBoolean(input: WrappedInput[Boolean]): BsonValue =
      new BsonBoolean(input.value)

    override protected def traverseChar(input: WrappedInput[Char]): BsonValue =
      new BsonString(input.value.toString)

    override protected def traverseDateTime(input: WrappedInput[DateTime]): BsonValue =
      new BsonDateTime(input.value.toDateTime(DateTimeZone.UTC).getMillis)

    override protected def traverseDouble(input: WrappedInput[Double]): BsonValue =
      new BsonDouble(input.value)

    override protected def traverseFloat(input: WrappedInput[Float]): BsonValue =
      new BsonDouble(input.value.toDouble)

    override protected def traverseInt(input: WrappedInput[Int]): BsonValue =
      new BsonInt32(input.value)

    override protected def traverseLong(input: WrappedInput[Long]): BsonValue =
      new BsonInt64(input.value)

    override protected def traverseString(input: WrappedInput[String]): BsonValue =
      new BsonString(input.value)

    override protected def constituentTypeKey[A : TypeKey](
      union: Union[A],
      input: WrappedInput[A])
    : TypeKey[_ <: A] =
      union.typeKeyForInstance(input.value).get

    override protected def stageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: WrappedInput[A])
    : Iterable[WrappedInput[B]] =
      Seq(input.asInstanceOf[WrappedInput[B]].copy(isUnionOrTopLevel = true))

    override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: WrappedInput[A],
      result: Iterable[BsonValue])
    : BsonValue =
      result.head.asDocument.append("_discriminator", new BsonString(typeKey[B].name))

    override protected def stageEmblemProps[A : TypeKey](
      emblem: Emblem[A],
      input: WrappedInput[A])
    : Iterable[PropInput[A, _]] = {
      def propInput[B](prop: EmblemProp[A, B]) = prop -> WrappedInput(prop.get(input.value), false)
      emblem.props.map(propInput(_))
    }

    override protected def unstageEmblemProps[A : TypeKey](
      emblem: Emblem[A],
      input: WrappedInput[A],
      result: Iterable[PropResult[A, _]])
    : TraverseResult[A] = {
      if (emblem.props.size == 1 && !input.isUnionOrTopLevel) {
        result.head._2
      } else {
        val document = new BsonDocument
        result.foreach {
          case (prop, propResult) =>
            if (!(prop.typeKey <:< optionAnyType) || propResult != BsonNull.VALUE) {
              document.append(prop.name, propResult)
            }
        }
        document
      }
    }

    override protected def stageOptionValue[A : TypeKey](
      input: WrappedInput[Option[A]])
    : Iterable[WrappedInput[A]] =
      input.value.toIterable.map(WrappedInput(_, false))

    override protected def unstageOptionValue[A : TypeKey](
      input: WrappedInput[Option[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Option[A]] =
      result.headOption.getOrElse(BsonNull.VALUE)

    override protected def stageSetElements[A : TypeKey](
      input: WrappedInput[Set[A]])
    : Iterable[WrappedInput[A]] =
      input.value.map(WrappedInput(_, false))

    override protected def unstageSetElements[A : TypeKey](
      input: WrappedInput[Set[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[Set[A]] =
      new BsonArray(result.toList.asJava)

    override protected def stageListElements[A : TypeKey](
      input: WrappedInput[List[A]])
    : Iterable[WrappedInput[A]] =
      input.value.map(WrappedInput(_, false))

    override protected def unstageListElements[A : TypeKey](
      input: WrappedInput[List[A]],
      result: Iterable[TraverseResult[A]])
    : TraverseResult[List[A]] =
      new BsonArray(result.toList.asJava)

  }

}
