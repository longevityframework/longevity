package emblem.traversors.sync

import emblem.exceptions.CouldNotTraverseException
import emblem.imports._
import emblem.jsonUtil.dateTimeFormatter
import org.joda.time.DateTime
import org.json4s.JsonAST._

/** translates json4s AST into emblematic types.
 *
 * @param emblemPool a pool of emblems
 * @param extractorPool a complete set of the extractors to use
 */
class JsonToEmblemTranslator extends Traversor {

  type TraverseInput[A] = JValue
  type TraverseResult[A] = A

  protected def traverseBoolean(input: JValue): Boolean = input match {
    case JBool(b) => b
    case _ => throw new CouldNotTraverseException(typeKey[Boolean])
  }

  protected def traverseChar(input: JValue): Char = input match {
    case JString(s) if s.length == 1 => s.head
    case _ => throw new CouldNotTraverseException(typeKey[Char])
  }

  protected def traverseDateTime(input: JValue): DateTime = input match {
    case JString(s) => dateTimeFormatter.parseDateTime(s)
    case _ => throw new CouldNotTraverseException(typeKey[DateTime])
  }

  protected def traverseDouble(input: JValue): Double = input match {
    case JDouble(d) => d
    case _ => throw new CouldNotTraverseException(typeKey[Double])
  }

  protected def traverseFloat(input: JValue): Float = input match {
    case JDouble(f) => f.toFloat
    case _ => throw new CouldNotTraverseException(typeKey[Float])
  }

  protected def traverseInt(input: JValue): Int = input match {
    case JInt(i) => i.toInt
    case _ => throw new CouldNotTraverseException(typeKey[Int])
  }

  protected def traverseLong(input: JValue): Long = input match {
    case JInt(i) => i.toLong
    case JLong(l) => l
    case _ => throw new CouldNotTraverseException(typeKey[Long])
  }

  protected def traverseString(input: JValue): String = input match {
    case JString(s) => s
    case _ => throw new CouldNotTraverseException(typeKey[String])
  }

  protected def stageEmblemProps[A <: HasEmblem : TypeKey](
    emblem: Emblem[A],
    input: JValue)
  : Iterable[PropInput[A, _]] = {
    input match {
      case JObject(fields) =>
        def fieldValue(name: String) = fields.find(_._1 == name).map(_._2).getOrElse(JNothing)
        def propInput[B](prop: EmblemProp[A, B]) = prop -> fieldValue(prop.name)
        emblem.props.map(propInput(_))
      case _ => throw new CouldNotTraverseException(typeKey[A])
    }
  }

  protected def unstageEmblemProps[A <: HasEmblem : TypeKey](
    emblem: Emblem[A],
    result: Iterable[PropResult[A, _]])
  : A = {
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
    extractor.inverse(rangeResult)

  protected def stageOptionValue[A : TypeKey](
    input: JValue)
  : Iterable[JValue] =
    input match {
      case JNothing => Seq()
      case _ => Seq(input)
    }

  protected def unstageOptionValue[A : TypeKey](
    input: JValue,
    result: Iterable[A])
  : Option[A] =
    result.headOption

  protected def stageSetElements[A : TypeKey](input: JValue): Iterable[JValue] = {
    input match {
      case JArray(elts) => elts
      case _ =>
        implicit val aTag = typeKey[A].tag
        throw new CouldNotTraverseException(typeKey[Set[A]])
    }
  }

  protected def unstageSetElements[A : TypeKey](input: JValue, result: Iterable[A]) : Set[A] =
    result.toSet

  protected def stageListElements[A : TypeKey](input: JValue): Iterable[JValue] =  {
    input match {
      case JArray(elts) => elts
      case _ =>
        implicit val aTag = typeKey[A].tag
        throw new CouldNotTraverseException(typeKey[List[A]])
    }
  }

  protected def unstageListElements[A : TypeKey](input: JValue, result: Iterable[A]): List[A] =
    result.toList

}

