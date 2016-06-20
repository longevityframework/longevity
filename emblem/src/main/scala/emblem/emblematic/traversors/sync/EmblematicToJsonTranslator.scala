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

/** translates emblematic types into json4s AST.
 * 
 * non top-level emblems with a single property will be inlined in the JSON.
 */
class EmblematicToJsonTranslator extends Traversor {

  /** translates an emblematic type into json4s AST */
  def translate[A : TypeKey](input: A): JValue = traverse[A](WrappedInput(input, true))

  case class WrappedInput[A](value: A, isTopLevel: Boolean)
  type TraverseInput[A] = WrappedInput[A]
  type TraverseResult[A] = JValue

  override protected def traverseBoolean(input: WrappedInput[Boolean]): JValue = JBool(input.value)

  override protected def traverseChar(input: WrappedInput[Char]): JValue = JString(input.value.toString)

  override protected def traverseDateTime(input: WrappedInput[DateTime]): JValue =
    JString(dateTimeFormatter.print(input.value))

  override protected def traverseDouble(input: WrappedInput[Double]): JValue = JDouble(input.value)

  override protected def traverseFloat(input: WrappedInput[Float]): JValue = JDouble(input.value.toDouble)

  override protected def traverseInt(input: WrappedInput[Int]): JValue = JInt(input.value)

  override protected def traverseLong(input: WrappedInput[Long]): JValue = JLong(input.value)

  override protected def traverseString(input: WrappedInput[String]): JValue = JString(input.value)

  override protected def constituentTypeKey[A : TypeKey](union: Union[A], input: WrappedInput[A])
  : TypeKey[_ <: A] =
    union.typeKeyForInstance(input.value).get

  override protected def stageUnion[A : TypeKey, B <: A : TypeKey](union: Union[A], input: WrappedInput[A])
  : Iterable[WrappedInput[B]] =
    Seq(WrappedInput(input.value.asInstanceOf[B], input.isTopLevel))

  override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
    union: Union[A],
    input: WrappedInput[A],
    result: Iterable[JValue])
  : JValue = {
    val fields = result.head.asInstanceOf[JObject].obj
    JObject(("discriminator", JString(typeKey[B].name)) :: fields)
  }

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
  : JValue = {
    if (emblem.props.size == 1 && !input.isTopLevel) {
      result.head._2
    } else {
      val jFields = result.toList.map { case (prop, result) => prop.name -> result }
      JObject(jFields)
    }
  }

  override protected def stageExtractor[Domain : TypeKey, Range : TypeKey](
    extractor: Extractor[Domain, Range],
    input: TraverseInput[Domain])
  : TraverseInput[Range] =
    WrappedInput(extractor.apply(input.value), input.isTopLevel)

  override protected def unstageExtractor[Domain : TypeKey, Range : TypeKey](
    extractor: Extractor[Domain, Range],
    rangeResult: TraverseResult[Range])
  : TraverseResult[Domain] =
    rangeResult

  override protected def stageOptionValue[A : TypeKey](
    input: TraverseInput[Option[A]])
  : Iterable[TraverseInput[A]] =
    input.value.toIterable.map(WrappedInput(_, input.isTopLevel))

  override protected def unstageOptionValue[A : TypeKey](
    input: WrappedInput[Option[A]],
    result: Iterable[JValue])
  : JValue =
    result.headOption.getOrElse(JNothing)

  override protected def stageSetElements[A : TypeKey](input: WrappedInput[Set[A]]): Iterable[WrappedInput[A]] =
    input.value.map(WrappedInput(_, false))

  override protected def unstageSetElements[A : TypeKey](
    input: WrappedInput[Set[A]],
    result: Iterable[JValue])
  : JValue =
    JArray(result.toList)

  override protected def stageListElements[A : TypeKey](input: WrappedInput[List[A]])
  : Iterable[WrappedInput[A]] =
    input.value.map(WrappedInput(_, false))

  override protected def unstageListElements[A : TypeKey](
    input: WrappedInput[List[A]],
    result: Iterable[JValue])
  : JValue =
    JArray(result.toList)

}
