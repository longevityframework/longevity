package longevity.testUtil

import scala.reflect.runtime.universe.typeOf
import emblem._
import longevity.exceptions.CouldNotGenerateException

// TODO scaladoc
class TestDataGenerator (
  private val shorthandPool: ShorthandPool = ShorthandPool()
  // TODO custon shorthand gens
) {

  private val random = new util.Random

  private type Func1[A] = () => A

  private val basicGenerators =
    TypeKeyMap[Any, Func1] + boolean _ + char _ + double _ + float _ + int _ + long _ + string _

  // TODO: specs
  // TODO add throws clause
  def any[A <: Any : TypeKey](): A = {
    val key = typeKey[A]
    shorthandOption()(key) getOrElse {
      basicOption()(key) getOrElse {
        throw new CouldNotGenerateException(key)
      }
    }
    // TODO: emblems
    // TODO: custom generators
  }

  // TODO: overload emblem to look up emblem in type key map

  @throws[CouldNotGenerateException]
  def emblem[T <: HasEmblem](emblem: Emblem[T]): T = {
    val builder = emblem.builder()
    emblem.props.foreach { prop => setEmblemProp(builder, prop) }
    builder.build()
  }

  @throws[CouldNotGenerateException]
  def shorthand[Long : TypeKey](): Long = shorthandOption[Long]() match {
    case Some(long) => long
    case None => throw new CouldNotGenerateException(typeKey[Long])
  }

  @throws[CouldNotGenerateException]
  def shorthand[Long](shorthand: Shorthand[Long, _]): Long = fullyTypedShorthand(shorthand)

  @inline def boolean(): Boolean = random.nextBoolean()

  @inline def char(): Char = {
    math.abs(random.nextInt % 62) match {
      case i if i < 26 => (i + 'A').toChar
      case i if i < 52 => (i - 26 + 'a').toChar
      case i => (i - 52 + '0').toChar
    }
  }

  @inline def double(): Double = random.nextDouble() 

  @inline def float(): Float = random.nextFloat() 

  @inline def int(): Int = random.nextInt()

  @inline def long(): Long = random.nextLong() 
  
  @inline def string(): String = string(8)

  @inline def string(len: Int): String = new String((1 to len).map(i => char()).toArray)

  def shorthandOption[Long : TypeKey](): Option[Long] = {
    shorthandPool.longTypeKeyToShorthand[Long] map { s => shorthand[Long](s) }
  }

  def basicOption[Basic : TypeKey](): Option[Basic] = {
    basicGenerators.get[Basic] map { gen => gen() }
  }

  private def setEmblemProp[T <: HasEmblem, U](builder: HasEmblemBuilder[T], prop: EmblemProp[T, U]): Unit = {
    builder.setProp(prop, any()(prop.typeKey))
  }

  private def fullyTypedShorthand[Long, Short](shorthand: Shorthand[Long, Short]): Long = {
    basicGenerators.get(shorthand.shortTypeKey) match {
      case Some(gen) => shorthand.unshorten(gen())
      case None => throw new CouldNotGenerateException(shorthand.shortTypeKey)
    }
  }

  private def isAlphaNumeric(c: Char) =
  (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')

}
