package emblem.emblematic.traversors.sync

import com.github.nscala_time.time.Implicits.richInt
import com.github.nscala_time.time.Imports.richDateTime
import emblem.TypeKey
import emblem.emblematic.Emblematic
import emblem.emblematic.Union
import org.joda.time.DateTime

/** generates test data for a pool of extractors, a pool of emblems, and some
 * custom generators. you can generate any kind of data you like by providing
 * the appropriate [[TypeKey]] to [[TestDataGenerator.generate]]. or you can use
 * the provided methods for generating specific kinds of data. if the generator
 * does not know how to generate for the type you requested, it will throw a
 * [[emblem.exceptions.CouldNotGenerateException]].
 *
 * out of the box, a TestDataGenerator knows how to generate the following basic
 * and collection types:
 *
 *   - boolean
 *   - char
 *   - double
 *   - float
 *   - int
 *   - long
 *   - string
 *   - list
 *   - option
 *   - set
 *
 * you can extend this behavior by supplying the generator with an
 * [[emblem.emblematic.Emblematic Emblematic]] and
 * [[CustomGenerator custom generators]].
 *
 * @param emblematic the emblematic types to use to generate test data for.
 * defaults to empty
 * 
 * @param customGeneratorPool custom generation functions. defaults to empty.
 * custom generators take precedence over all other generators
 */
class TestDataGenerator (
  override protected val emblematic: Emblematic = Emblematic.empty,
  override protected val customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty)
extends Generator {

  private val random = new util.Random

  override protected def constituentTypeKey[A : TypeKey](union: Union[A]): TypeKey[_ <: A] = {
    val numConstituents = union.constituents.size
    union.constituentKeys.toSeq(math.abs(int % numConstituents))
  } 

  override protected def optionSize[A : TypeKey]: Int = math.abs(int % 2)
  override protected def setSize[A : TypeKey]: Int = math.abs(int % 4)
  override protected def listSize[A : TypeKey]: Int = math.abs(int % 4)

  /** generates a boolean that is true around half the time */
  def boolean: Boolean = random.nextBoolean()

  /** generates a char that is either a decimal digit or a letter (upper or
   * lowercase) from the Roman alphabet
   */
  def char: Char = math.abs(random.nextInt % 62) match {
    case i if i < 26 => (i + 'A').toChar
    case i if i < 52 => (i - 26 + 'a').toChar
    case i => (i - 52 + '0').toChar
  }

  /** generates a date-time */
  def dateTime = DateTime.now + (random.nextInt % 10000).millis

  /** generates a double */
  def double: Double = random.nextDouble() 

  /** generates a float */
  def float: Float = random.nextFloat() 

  /** generates an int */
  def int: Int = random.nextInt()

  /** generates a long */
  def long: Long = random.nextLong() 
  
  /** generates a string of length 8 */
  def string: String = string(8)

  /** generates a string of the specified length */
  def string(length: Int): String = new String((1 to length).map(i => char).toArray)

}
