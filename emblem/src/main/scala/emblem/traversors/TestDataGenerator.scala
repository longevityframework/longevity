package emblem.traversors

import emblem._
import emblem.reflectionUtil.makeTypeTag
import emblem.traversors.Generator._
import scala.reflect.runtime.universe.typeOf

/** Generates test data for a pool of extractors, a pool of emblems, and some custom generators. You can
 * generate any kind of data you like by providing the appropriate [[TypeKey]] to [[TestDataGenerator.generate]].
 * Or you can use the provided methods for generating specific kinds of data. If the generator does not know
 * how to generate for the type you requested, it will throw a [[emblem.exceptions.CouldNotGenerateException]].
 *
 * Out of the box, a TestDataGenerator knows how to generate the following basic and collection types:
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
 * You can extend this behavior by supplying the generator with [[Extractor extractors]], [[Emblem emblems]],
 * and [[CustomGenerator custom generators]].
 *
 * @param extractorPool the extractors to generate test data for. defaults to empty
 * @param emblemPool the emblems to generate test data for. defaults to empty
 * @param customGenerators custom generation functions. defaults to empty. custom generators take precedence
 * over all other generators
 */
class TestDataGenerator (
  override protected val emblemPool: EmblemPool = EmblemPool.empty,
  override protected val extractorPool: ExtractorPool = ExtractorPool.empty,
  override protected val customGenerators: CustomGeneratorPool = CustomGeneratorPool.empty
) extends Generator {

  private val random = new util.Random

  /** Generates a boolean that is true around half the time */
  def boolean: Boolean = random.nextBoolean()

  /** Generates a char that is either a decimal digit or a letter (upper or lowercase) from the Roman
   * alphabet */
  def char: Char = math.abs(random.nextInt % 62) match {
    case i if i < 26 => (i + 'A').toChar
    case i if i < 52 => (i - 26 + 'a').toChar
    case i => (i - 52 + '0').toChar
  }

  /** Generates a double */
  def double: Double = random.nextDouble() 

  /** Generates a float */
  def float: Float = random.nextFloat() 

  /** Generates an int */
  def int: Int = random.nextInt()

  /** Generates a long */
  def long: Long = random.nextLong() 
  
  /** Generates a string of length 8 */
  def string: String = string(8)

  /** Generates a string of the specified length */
  def string(length: Int): String = new String((1 to length).map(i => char).toArray)

  protected def option[A](a: => A): Option[A] = if (boolean) Some(a) else None

  protected def set[A](a: => A): Set[A] = math.abs(int % 4) match {
    case 0 => Set[A]()
    case 1 => Set[A](a)
    case 2 => Set[A](a, a)
    case 3 => Set[A](a, a, a)
  }

  protected def list[A](a: => A): List[A] = math.abs(int % 4) match {
    case 0 => List[A]()
    case 1 => List[A](a)
    case 2 => List[A](a, a)
    case 3 => List[A](a, a, a)
  }

}
