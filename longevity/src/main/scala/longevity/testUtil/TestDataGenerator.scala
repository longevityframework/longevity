package longevity.testUtil

import scala.reflect.runtime.universe.typeOf
import emblem._
import longevity.exceptions.CouldNotGenerateException
import TestDataGenerator.GeneratorFunction

// TODO scaladoc
object TestDataGenerator {

  type GeneratorFunction[A] = Function1[TestDataGenerator, A]

}

// TODO scaladoc
class TestDataGenerator (
  private val shorthandPool: ShorthandPool = ShorthandPool(),
  private val emblemPool: TypeKeyMap[HasEmblem, Emblem] = TypeKeyMap[HasEmblem, Emblem](),
  private val customGenerators: TypeKeyMap[Any, GeneratorFunction] = TypeKeyMap[Any, GeneratorFunction]()
) {

  private val random = new util.Random

  private val basicGenerators =
    TypeKeyMap[Any, Function0] + boolean _ + char _ + double _ + float _ + int _ + long _ + string _

  @throws[CouldNotGenerateException]
  def any[A : TypeKey]: A = anyOption[A] getOrElse {
    throw new CouldNotGenerateException(typeKey[A])
  }

  @throws[CouldNotGenerateException]
  def custom[A : TypeKey]: A = customOption[A] match {
    case Some(a) => a
    case None => throw new CouldNotGenerateException(typeKey[A])
  }

  @throws[CouldNotGenerateException]
  def emblem[A <: HasEmblem : TypeKey]: A = emblemOption[A] match {
    case Some(a) => a
    case None => throw new CouldNotGenerateException(typeKey[A])
  }

  @throws[CouldNotGenerateException]
  def shorthand[Long : TypeKey](): Long = shorthandOption[Long] match {
    case Some(long) => long
    case None => throw new CouldNotGenerateException(typeKey[Long])
  }

  @inline def boolean: Boolean = random.nextBoolean()

  @inline def char: Char = math.abs(random.nextInt % 62) match {
    case i if i < 26 => (i + 'A').toChar
    case i if i < 52 => (i - 26 + 'a').toChar
    case i => (i - 52 + '0').toChar
  }

  @inline def double: Double = random.nextDouble() 

  @inline def float: Float = random.nextFloat() 

  @inline def int: Int = random.nextInt()

  @inline def long: Long = random.nextLong() 
  
  @inline def string: String = string(8)

  @inline def string(len: Int): String = new String((1 to len).map(i => char).toArray)

  // custom generators have to come first. after that order is immaterial
  private def anyOption[A : TypeKey]: Option[A] =
    customOption orElse emblemOptionFromAny orElse shorthandOption orElse basicOption

  private def customOption[A : TypeKey]: Option[A] = customGenerators.get[A] map { gen => gen(this) }

  private def emblemOptionFromAny[T : TypeKey]: Option[T] = {
    val keyOption = hasEmblemTypeKeyOption(typeKey[T])
    keyOption flatMap { k => emblemOption(k) }
  }

  private def hasEmblemTypeKeyOption[T : TypeKey, U <: T with HasEmblem]: Option[TypeKey[U]] =
    if (typeKey[T].tpe <:< typeOf[HasEmblem])
      Some(typeKey[T].asInstanceOf[TypeKey[U]])
    else
      None

  private def emblemOption[T <: HasEmblem : TypeKey]: Option[T] =
    emblemPool.get(typeKey[T]) map { e => genFromEmblem(e) }

  // todo make private, rename to genEmblem
  private def genFromEmblem[T <: HasEmblem](emblem: Emblem[T]): T = {
    val builder = emblem.builder()
    emblem.props.foreach { prop => setEmblemProp(builder, prop) }
    builder.build()
  }

  private def shorthandOption[Long : TypeKey]: Option[Long] =
    shorthandPool.longTypeKeyToShorthand[Long] map { s => genFromShorthand[Long](s) }

  private def genFromShorthand[Long](shorthand: Shorthand[Long, _]): Long = genFromFullyTypedShorthand(shorthand)

  // http://scabl.blogspot.com/2015/01/introduce-type-param-pattern.html
  private def genFromFullyTypedShorthand[Long, Short](shorthand: Shorthand[Long, Short]): Long =
    basicGenerators.get(shorthand.shortTypeKey) match {
      case Some(gen) => shorthand.unshorten(gen())
      case None => throw new CouldNotGenerateException(shorthand.shortTypeKey)
    }

  private def basicOption[Basic : TypeKey]: Option[Basic] = basicGenerators.get[Basic] map { gen => gen() }

  private def setEmblemProp[T <: HasEmblem, U](builder: HasEmblemBuilder[T], prop: EmblemProp[T, U]): Unit =
    builder.setProp(prop, any(prop.typeKey))

  private def isAlphaNumeric(c: Char) =
    (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')

}
