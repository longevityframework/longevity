package emblem.emblematic.traversors.sync

import emblem.emblematic.Emblem
import emblem.emblematic.EmblemProp
import emblem.emblematic.Extractor
import emblem.TypeKey
import emblem.emblematic.Union
import emblem.jsonUtil.dateTimeFormatter
import emblem.typeKey
import org.joda.time.DateTime
import org.json4s.JsonAST.JArray
import org.json4s.JsonAST.JBool
import org.json4s.JsonAST.JDouble
import org.json4s.JsonAST.JInt
import org.json4s.JsonAST.JLong
import org.json4s.JsonAST.JNothing
import org.json4s.JsonAST.JObject
import org.json4s.JsonAST.JString
import org.json4s.JsonAST.JValue

/** translates emblematic types into json4s AST */
class EmblematicToJsonTranslator extends Traversor {

  type TraverseInput[A] = A
  type TraverseResult[A] = JValue

  override protected def traverseBoolean(input: Boolean): JValue = JBool(input)

  override protected def traverseChar(input: Char): JValue = JString(input.toString)

  override protected def traverseDateTime(input: DateTime): JValue = JString(dateTimeFormatter.print(input))

  override protected def traverseDouble(input: Double): JValue = JDouble(input)

  override protected def traverseFloat(input: Float): JValue = JDouble(input.toDouble)

  override protected def traverseInt(input: Int): JValue = JInt(input)

  override protected def traverseLong(input: Long): JValue = JLong(input)

  override protected def traverseString(input: String): JValue = JString(input)

  override protected def constituentTypeKey[A : TypeKey](union: Union[A], input: A): TypeKey[_ <: A] =
    union.typeKeyForInstance(input).get

  override protected def stageUnion[A : TypeKey, B <: A : TypeKey](union: Union[A], input: A)
  : Iterable[B] =
    Seq(input.asInstanceOf[B])

  override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
    union: Union[A],
    input: A,
    result: Iterable[JValue])
  : JValue = {
    val fields = result.head.asInstanceOf[JObject].obj
    JObject(("discriminator", JString(typeKey[B].name)) :: fields)
  }

  override protected def stageEmblemProps[A : TypeKey](
    emblem: Emblem[A],
    input: A)
  : Iterable[PropInput[A, _]] = {
    def propInput[B](prop: EmblemProp[A, B]) = prop -> prop.get(input)
    emblem.props.map(propInput(_))
  }

  override protected def unstageEmblemProps[A : TypeKey](
    emblem: Emblem[A],
    result: Iterable[PropResult[A, _]])
  : JValue = {
    val jFields = result.toList.map { case (prop, result) => prop.name -> result }
    JObject(jFields)
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
    input: Option[A],
    result: Iterable[JValue])
  : JValue =
    result.headOption.getOrElse(JNothing)

  override protected def stageSetElements[A : TypeKey](input: Set[A]): Iterable[A] = input

  override protected def unstageSetElements[A : TypeKey](
    input: TraverseInput[Set[A]],
    result: Iterable[JValue])
  : JValue =
    JArray(result.toList)

  override protected def stageListElements[A : TypeKey](input: List[A]): Iterable[A] = input

  override protected def unstageListElements[A : TypeKey](
    input: List[A],
    result: Iterable[JValue])
  : JValue =
    JArray(result.toList)

}

