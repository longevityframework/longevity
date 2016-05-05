package emblem.traversors.sync

import emblem.Emblem
import emblem.EmblemPool
import emblem.EmblemProp
import emblem.Extractor
import emblem.ExtractorPool
import emblem.TypeKey
import emblem.Union
import emblem.exceptions.CouldNotTraverseException
import emblem.jsonUtil.dateTimeFormatter
import emblem.typeKey
import org.joda.time.DateTime
import org.json4s.JsonAST._

/** translates json4s AST into emblematic types */
class JsonToEmblemTranslator extends Traversor {

  type TraverseInput[A] = JValue
  type TraverseResult[A] = A

  override protected def traverseBoolean(input: JValue): Boolean = input match {
    case JBool(b) => b
    case _ => throw new CouldNotTraverseException(typeKey[Boolean])
  }

  override protected def traverseChar(input: JValue): Char = input match {
    case JString(s) if s.length == 1 => s.head
    case _ => throw new CouldNotTraverseException(typeKey[Char])
  }

  override protected def traverseDateTime(input: JValue): DateTime = input match {
    case JString(s) => dateTimeFormatter.parseDateTime(s)
    case _ => throw new CouldNotTraverseException(typeKey[DateTime])
  }

  override protected def traverseDouble(input: JValue): Double = input match {
    case JDouble(d) => d
    case _ => throw new CouldNotTraverseException(typeKey[Double])
  }

  override protected def traverseFloat(input: JValue): Float = input match {
    case JDouble(f) => f.toFloat
    case _ => throw new CouldNotTraverseException(typeKey[Float])
  }

  override protected def traverseInt(input: JValue): Int = input match {
    case JInt(i) => i.toInt
    case _ => throw new CouldNotTraverseException(typeKey[Int])
  }

  override protected def traverseLong(input: JValue): Long = input match {
    case JInt(i) => i.toLong
    case JLong(l) => l
    case _ => throw new CouldNotTraverseException(typeKey[Long])
  }

  override protected def traverseString(input: JValue): String = input match {
    case JString(s) => s
    case _ => throw new CouldNotTraverseException(typeKey[String])
  }

  override protected def constituentTypeKey[A : TypeKey](union: Union[A], input: JValue): TypeKey[_ <: A] = {
    val discriminator = input.asInstanceOf[JObject].values("discriminator").asInstanceOf[String]
    union.typeKeyForName(discriminator).get
  }

  override protected def stageUnion[A : TypeKey, B <: A : TypeKey](union: Union[A], input: JValue)
  : Iterable[JValue] =
    Seq(input)

  override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
    union: Union[A],
    input: JValue,
    result: Iterable[B])
  : A =
    result.head

  override protected def stageEmblemProps[A : TypeKey](
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

  override protected def unstageEmblemProps[A : TypeKey](
    emblem: Emblem[A],
    result: Iterable[PropResult[A, _]])
  : A = {
    val builder = emblem.builder()
    result.foreach { case (prop, propResult) => builder.setProp(prop, propResult) }
    builder.build()
  }

  override protected def stageExtractor[Domain : TypeKey, Range : TypeKey](
    extractor: Extractor[Domain, Range],
    input: TraverseInput[Domain])
  : TraverseInput[Range] =
    input

  override protected def unstageExtractor[Domain : TypeKey, Range : TypeKey](
    extractor: Extractor[Domain, Range],
    rangeResult: TraverseResult[Range])
  : TraverseResult[Domain] =
    extractor.inverse(rangeResult)

  override protected def stageOptionValue[A : TypeKey](
    input: JValue)
  : Iterable[JValue] =
    input match {
      case JNothing => Seq()
      case _ => Seq(input)
    }

  override protected def unstageOptionValue[A : TypeKey](
    input: JValue,
    result: Iterable[A])
  : Option[A] =
    result.headOption

  override protected def stageSetElements[A : TypeKey](input: JValue): Iterable[JValue] = {
    input match {
      case JArray(elts) => elts
      case _ =>
        implicit val aTag = typeKey[A].tag
        throw new CouldNotTraverseException(typeKey[Set[A]])
    }
  }

  override protected def unstageSetElements[A : TypeKey](input: JValue, result: Iterable[A]) : Set[A] =
    result.toSet

  override protected def stageListElements[A : TypeKey](input: JValue): Iterable[JValue] =  {
    input match {
      case JArray(elts) => elts
      case _ =>
        implicit val aTag = typeKey[A].tag
        throw new CouldNotTraverseException(typeKey[List[A]])
    }
  }

  override protected def unstageListElements[A : TypeKey](input: JValue, result: Iterable[A]): List[A] =
    result.toList

}

