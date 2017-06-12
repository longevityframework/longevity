package longevity.emblem.emblematic.traversors.sync

import longevity.emblem.emblematic.Emblem
import longevity.emblem.emblematic.EmblemProp
import typekey.TypeKey
import longevity.emblem.emblematic.Union
import longevity.emblem.exceptions.CouldNotTraverseException
import longevity.emblem.jsonUtil.dateTimeFormatter
import typekey.typeKey
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

/** translates json4s AST into emblematic types.
 * 
 * expects JSON for non top-level emblems with a single property to inline those
 * emblems. does not expect inlined unions.
 */
private[longevity] class JsonToEmblematicTranslator extends Traversor {

  /** translates json4s AST into emblematic types */
  def translate[A : TypeKey](input: JValue): A = traverse[A](WrappedInput(input, true))

  case class WrappedInput(value: JValue, isUnionOrTopLevel: Boolean)
  type TraverseInput[A] = WrappedInput
  type TraverseResult[A] = A

  override protected def traverseBoolean(input: WrappedInput): Boolean = input.value match {
    case JBool(b) => b
    case _ => throw new CouldNotTraverseException(typeKey[Boolean])
  }

  override protected def traverseChar(input: WrappedInput): Char = input.value match {
    case JString(s) if s.length == 1 => s.head
    case _ => throw new CouldNotTraverseException(typeKey[Char])
  }

  override protected def traverseDateTime(input: WrappedInput): DateTime = input.value match {
    case JString(s) => dateTimeFormatter.parseDateTime(s)
    case _ => throw new CouldNotTraverseException(typeKey[DateTime])
  }

  override protected def traverseDouble(input: WrappedInput): Double = input.value match {
    case JDouble(d) => d
    case _ => throw new CouldNotTraverseException(typeKey[Double])
  }

  override protected def traverseFloat(input: WrappedInput): Float = input.value match {
    case JDouble(f) => f.toFloat
    case _ => throw new CouldNotTraverseException(typeKey[Float])
  }

  override protected def traverseInt(input: WrappedInput): Int = input.value match {
    case JInt(i) => i.toInt
    case _ => throw new CouldNotTraverseException(typeKey[Int])
  }

  override protected def traverseLong(input: WrappedInput): Long = input.value match {
    case JInt(i) => i.toLong
    case JLong(l) => l
    case _ => throw new CouldNotTraverseException(typeKey[Long])
  }

  override protected def traverseString(input: WrappedInput): String = input.value match {
    case JString(s) => s
    case _ => throw new CouldNotTraverseException(typeKey[String])
  }

  override protected def constituentTypeKey[A : TypeKey](union: Union[A], input: WrappedInput)
  : TypeKey[_ <: A] = {
    val discriminator = input.value.asInstanceOf[JObject].values("discriminator").asInstanceOf[String]
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

  override protected def stageEmblemProps[A : TypeKey](
    emblem: Emblem[A],
    input: WrappedInput)
  : Iterable[PropInput[A, _]] = {
    if (emblem.props.size == 1 && !input.isUnionOrTopLevel) {
      Seq(emblem.props.head -> WrappedInput(input.value, false))
    } else {
      input.value match {
        case JObject(fields) =>
          def fieldValue(name: String) = fields.find(_._1 == name).map(_._2).getOrElse(JNothing)
          def propInput[B](prop: EmblemProp[A, B]) = prop -> WrappedInput(fieldValue(prop.name), false)
          emblem.props.map(propInput(_))
        case _ => throw new CouldNotTraverseException(typeKey[A])
      }
    }
  }

  override protected def unstageEmblemProps[A : TypeKey](
    emblem: Emblem[A],
    input: WrappedInput,
    result: Iterable[PropResult[A, _]])
  : A = {
    val builder = emblem.builder()
    result.foreach { case (prop, propResult) => builder.setProp(prop, propResult) }
    builder.build()
  }

  override protected def stageOptionValue[A : TypeKey](
    input: WrappedInput)
  : Iterable[WrappedInput] =
    input.value match {
      case JNothing => Seq()
      case _ => Seq(input.copy(isUnionOrTopLevel = false))
    }

  override protected def unstageOptionValue[A : TypeKey](
    input: WrappedInput,
    result: Iterable[A])
  : Option[A] =
    result.headOption

  override protected def stageSetElements[A : TypeKey](input: WrappedInput): Iterable[WrappedInput] = {
    input.value match {
      case JArray(elts) => elts.map(WrappedInput(_, false))
      case _ =>
        implicit val aTag = typeKey[A].tag
        throw new CouldNotTraverseException(typeKey[Set[A]])
    }
  }

  override protected def unstageSetElements[A : TypeKey](input: WrappedInput, result: Iterable[A]) : Set[A] =
    result.toSet

  override protected def stageListElements[A : TypeKey](input: WrappedInput): Iterable[WrappedInput] =  {
    input.value match {
      case JArray(elts) => elts.map(WrappedInput(_, false))
      case _ =>
        implicit val aTag = typeKey[A].tag
        throw new CouldNotTraverseException(typeKey[List[A]])
    }
  }

  override protected def unstageListElements[A : TypeKey](input: WrappedInput, result: Iterable[A]): List[A] =
    result.toList

}

