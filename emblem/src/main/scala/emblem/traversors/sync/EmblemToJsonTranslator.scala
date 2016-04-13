package emblem.traversors.sync

import emblem.Emblem
import emblem.EmblemPool
import emblem.EmblemProp
import emblem.Extractor
import emblem.ExtractorPool
import emblem.HasEmblem
import emblem.typeKey
import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.jsonUtil.dateTimeFormatter
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.json4s.JsonAST._

/** translates emblematic types into json4s AST.
 *
 * @param emblemPool a pool of emblems
 * @param extractorPool a complete set of the extractors to use
 */
class EmblemToJsonTranslator extends Traversor {

  type TraverseInput[A] = A
  type TraverseResult[A] = JValue

  protected def traverseBoolean(input: Boolean): JValue = JBool(input)

  protected def traverseChar(input: Char): JValue = JString(input.toString)

  protected def traverseDateTime(input: DateTime): JValue = JString(dateTimeFormatter.print(input))

  protected def traverseDouble(input: Double): JValue = JDouble(input)

  protected def traverseFloat(input: Float): JValue = JDouble(input.toDouble)

  protected def traverseInt(input: Int): JValue = JInt(input)

  protected def traverseLong(input: Long): JValue = JLong(input)

  protected def traverseString(input: String): JValue = JString(input)

  protected def stageEmblemProps[A <: HasEmblem : TypeKey](
    emblem: Emblem[A],
    input: A)
  : Iterable[PropInput[A, _]] = {
    def propInput[B](prop: EmblemProp[A, B]) = prop -> prop.get(input)
    emblem.props.map(propInput(_))
  }

  protected def unstageEmblemProps[A <: HasEmblem : TypeKey](
    emblem: Emblem[A],
    result: Iterable[PropResult[A, _]])
  : JValue = {
    val jFields = result.toList.map { case (prop, result) => prop.name -> result }
    JObject(jFields)
  }

  protected def stageExtractor[Domain : TypeKey, Range : TypeKey](
    extractor: Extractor[Domain, Range],
    input: TraverseInput[Domain])
  : TraverseInput[Range] =
    extractor.apply(input)

  protected def unstageExtractor[Domain : TypeKey, Range : TypeKey](
    extractor: Extractor[Domain, Range],
    rangeResult: TraverseResult[Range])
  : TraverseResult[Domain] =
    rangeResult

  protected def stageOptionValue[A : TypeKey](
    input: TraverseInput[Option[A]])
  : Iterable[TraverseInput[A]] =
    input.toIterable

  protected def unstageOptionValue[A : TypeKey](
    input: Option[A],
    result: Iterable[JValue])
  : JValue =
    result.headOption.getOrElse(JNothing)

  protected def stageSetElements[A : TypeKey](input: Set[A]): Iterable[A] = input

  protected def unstageSetElements[A : TypeKey](
    input: TraverseInput[Set[A]],
    result: Iterable[JValue])
  : JValue =
    JArray(result.toList)

  protected def stageListElements[A : TypeKey](input: List[A]): Iterable[A] = input

  protected def unstageListElements[A : TypeKey](
    input: List[A],
    result: Iterable[JValue])
  : JValue =
    JArray(result.toList)

}

