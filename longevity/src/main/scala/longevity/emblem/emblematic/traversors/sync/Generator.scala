package longevity.emblem.emblematic.traversors.sync

import typekey.TypeKey
import longevity.emblem.emblematic.Emblem
import longevity.emblem.emblematic.Emblematic
import longevity.emblem.emblematic.Union
import longevity.emblem.exceptions.CouldNotGenerateException
import longevity.emblem.exceptions.CouldNotTraverseException
import typekey.TypeBoundFunction
import org.joda.time.DateTime

/** recursively generates a data structure by type.
 *
 * you can generate arbritrary data to your liking by implementing the protected
 * vals and defs in this interface. as of yet, i haven't been able to generate
 * the scaladoc for those protected methods. sorry about that.
 *
 * @see [[TestDataGenerator]] for an example usage
 */
private[longevity] trait Generator {

  /** generates data for the specified type `A`
   *
   * @tparam A the type of data to generate
   * @return the generated data
   * @throws emblem.exceptions.CouldNotGenerateException when we encounter a type in the recursive traversal
   * that we don't know how to generate for
   */
  def generate[A : TypeKey]: A = try {
    traversor.traverse[A](())
  } catch {
    case e: CouldNotTraverseException => throw new CouldNotGenerateException(e.typeKey, e)
  }

  /** the emblematic types to use in the recursive generation */
  protected val emblematic: Emblematic = Emblematic.empty

  /** the custom generators to use in the recursive generation */
  protected val customGeneratorPool: CustomGeneratorPool = CustomGeneratorPool.empty

  /** generates a type key for the union constituent
   *
   * @tparam A the type of the [[emblem.emblematic.Union Union]] object to traverse
   * @param union the union
   * @return the constituent type key
   */
  protected def constituentTypeKey[A : TypeKey](union: Union[A]): TypeKey[_ <: A]

  /** returns the size of the option to be generated. a return value of `0` will
   * generate a `None`, and a return value of `1` (or anything other than `0`)
   * will generate a `Some`.
   * 
   * @tparam A the type of the optional value
   */
  protected def optionSize[A : TypeKey]: Int

  /** returns the size of the set to be generated. a negative return value will
   * result in an empty set.
   * 
   * @tparam A the type of the set elements
   */
  protected def setSize[A : TypeKey]: Int

  /** returns the size of the list to be generated. a negative return value will
   * result in an empty list.
   * 
   * @tparam A the type of the list elements
   */
  protected def listSize[A : TypeKey]: Int

  /** generates a boolean */
  protected def boolean: Boolean

  /** generates a char */
  protected def char: Char

  /** generates a double */
  protected def double: Double

  /** generates a date-time */
  protected def dateTime: DateTime

  /** generates a float */
  protected def float: Float

  /** generates an int */
  protected def int: Int

  /** generates a long */
  protected def long: Long
  
  /** generates a string */
  protected def string: String

  private val traversor = new Traversor {

    type TraverseInput[A] = Unit
    type TraverseResult[A] = A

    def traverseBoolean(input: Unit): Boolean = boolean

    def traverseChar(input: Unit): Char = char

    def traverseDateTime(input: Unit): DateTime = dateTime

    def traverseDouble(input: Unit): Double = double

    def traverseFloat(input: Unit): Float = float

    def traverseInt(input: Unit): Int = int

    def traverseLong(input: Unit): Long = long

    def traverseString(input: Unit): String = string

    override protected val emblematic = Generator.this.emblematic

    override protected val customTraversors = {
      class GenCustomTraversor[A](val customGenerator: CustomGenerator[A]) extends CustomTraversor[A] {
        def apply[B <: A : TypeKey](input: Unit): B = customGenerator.apply[B](Generator.this)
      }
      val generatorToTraversor = new TypeBoundFunction[Any, CustomGenerator, CustomTraversor] {
        def apply[A](generator: CustomGenerator[A]): CustomTraversor[A] = new GenCustomTraversor(generator)
      }
      customGeneratorPool.mapValues(generatorToTraversor)
    }

    override protected def constituentTypeKey[A : TypeKey](
      union: Union[A],
      input: TraverseInput[A])
    : TypeKey[_ <: A] =
      Generator.this.constituentTypeKey(union)

    override protected def stageUnion[A : TypeKey, B <: A : TypeKey](union: Union[A], input: Unit)
    : Iterable[TraverseInput[B]] =
      Seq(())

    override protected def unstageUnion[A : TypeKey, B <: A : TypeKey](
      union: Union[A],
      input: Unit,
      result: Iterable[B])
    : A =
      result.head

    override protected def stageEmblemProps[A : TypeKey](emblem: Emblem[A], input: Unit)
    : Iterable[PropInput[A, _]] =
      emblem.props.map((_, ()))

    override protected def unstageEmblemProps[A : TypeKey](
      emblem: Emblem[A],
      input: Unit,
      result: Iterable[PropResult[A, _]])
    : A = {
      val builder = emblem.builder()
      result.foreach { case (prop, propResult) => builder.setProp(prop, propResult) }
      builder.build()
    }

    override protected def stageOptionValue[A : TypeKey](input: Unit): Iterable[Unit] =
      List.fill(optionSize)(())

    override protected def unstageOptionValue[A : TypeKey](input: Unit, result: Iterable[A]): Option[A] =
      result.headOption

    override protected def stageSetElements[A : TypeKey](input: Unit): Iterable[Unit] =
      List.fill(listSize)(())

    override protected def unstageSetElements[A : TypeKey](input: Unit, result: Iterable[A]): Set[A] =
      result.toSet

    override protected def stageListElements[A : TypeKey](input: Unit): Iterable[Unit] =
      List.fill(setSize)(())

    override protected def unstageListElements[A : TypeKey](input: Unit, result: Iterable[A]): List[A] =
      result.toList

  }

}
