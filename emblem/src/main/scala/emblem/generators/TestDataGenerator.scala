package emblem.generators

import scala.reflect.runtime.universe.typeOf
import emblem._
import emblem.reflectionUtil.makeTypeTag
import emblem.exceptions.CouldNotGenerateException
import TestDataGenerator._

/** holds types and zero values used by the [[TestDataGenerator]] */
object TestDataGenerator {

  /** A [[TypeKeyMap]] for [[Emblem Emblems]] */
  type EmblemPool = TypeKeyMap[HasEmblem, Emblem]

  /** An empty emblem pool */
  def emptyEmblemPool: EmblemPool = TypeKeyMap[HasEmblem, Emblem]()

  /** A generator function for type A. This kind of function takes a [[TestDataGenerator]] as argument,
   * so that it can generate complex values based on more primitive values. */
  type GeneratorFunction[A] = Function1[TestDataGenerator, A]

  /** A [[TypeKeyMap]] for [[GeneratorFunction generator functions]] */
  type CustomGenerators = TypeKeyMap[Any, GeneratorFunction]

  /** An empty map of [[GeneratorFunction generator functions]] */
  def emptyCustomGenerators: CustomGenerators = TypeKeyMap[Any, GeneratorFunction]()

}

/** Generates test data for a pool of shorthands, a pool of emblems, and some custom generators.
 * TODO elaborate */
class TestDataGenerator (
  private val shorthandPool: ShorthandPool = ShorthandPool(),
  private val emblemPool: TypeKeyMap[HasEmblem, Emblem] = emptyEmblemPool,
  private val customGenerators: CustomGenerators = emptyCustomGenerators
) {

  private val random = new util.Random

  private val basicGenerators =
    TypeKeyMap[Any, Function0] + boolean _ + char _ + double _ + float _ + int _ + long _ + string _

  /** Generates test data for the specified type `A`. */
  @throws[CouldNotGenerateException]("when we cannot find a way to generate something of type A")
  def any[A : TypeKey]: A = anyOption[A] getOrElse {
    throw new CouldNotGenerateException(typeKey[A])
  }

  /** Generates test data for the specified type `A` according to a custom generator */
  @throws[CouldNotGenerateException]("when there is no custom generator for type A")
  def custom[A : TypeKey]: A = customOption[A] getOrElse {
    throw new CouldNotGenerateException(typeKey[A])
  }

  /** Generates test data for the specified type `A` via an emblem in the pool */
  @throws[CouldNotGenerateException]("when there is no emblem in the pool for type A")
  def emblem[A <: HasEmblem : TypeKey]: A = emblemOption[A] getOrElse {
    throw new CouldNotGenerateException(typeKey[A])
  }

  /** Generates test data for the specified type `A` via a shorthand in the pool */
  @throws[CouldNotGenerateException]("when there is no shorthand in the pool for type A")
  def shorthand[Long : TypeKey]: Long = shorthandOption[Long] getOrElse {
    throw new CouldNotGenerateException(typeKey[Long])
  }

  /** Generates an option containing (or not) an element of type A. Generates `Some` and `None` values
   * at about a 50-50 ratio. */
  @throws[CouldNotGenerateException]("when we cannot generate the contained type A")
  def option[A : TypeKey]: Option[A] = if (boolean) Some(any[A]) else None

  /** Generates a set containing (or not) elements of type A. Generates sets of size 0, 1, 2 and 3
   * at about a 25-25-25-25 ratio. */
  def set[A : TypeKey]: Set[A] = math.abs(int % 4) match {
    case 0 => Set[A]()
    case 1 => Set[A](any[A])
    case 2 => Set[A](any[A], any[A])
    case 3 => Set[A](any[A], any[A], any[A])
  }

  /** Generates a list containing (or not) elements of type A. Generates lists of size 0, 1, 2 and 3
   * at about a 25-25-25-25 ratio. */
  def list[A : TypeKey]: List[A] = math.abs(int % 4) match {
    case 0 => List[A]()
    case 1 => List[A](any[A])
    case 2 => List[A](any[A], any[A])
    case 3 => List[A](any[A], any[A], any[A])
  }

  // TODO scaladoc

  def boolean: Boolean = random.nextBoolean()

  def char: Char = math.abs(random.nextInt % 62) match {
    case i if i < 26 => (i + 'A').toChar
    case i if i < 52 => (i - 26 + 'a').toChar
    case i => (i - 52 + '0').toChar
  }

  def double: Double = random.nextDouble() 

  def float: Float = random.nextFloat() 

  def int: Int = random.nextInt()

  def long: Long = random.nextLong() 
  
  def string: String = string(8)

  def string(len: Int): String = new String((1 to len).map(i => char).toArray)

  // custom generators have to come first. after that order is immaterial
  private def anyOption[A : TypeKey]: Option[A] =
    customOption orElse
    emblemOptionFromAny orElse
    shorthandOption orElse
    optionOption orElse
    setOption orElse
    listOption orElse
    basicOption

  private def customOption[A : TypeKey]: Option[A] = customGenerators.get[A] map { gen => gen(this) }

  private def emblemOptionFromAny[A : TypeKey]: Option[A] = {
    val keyOption = hasEmblemTypeKeyOption(typeKey[A])
    keyOption flatMap { k => emblemOption(k) }
  }

  private def hasEmblemTypeKeyOption[A : TypeKey, B <: A with HasEmblem]: Option[TypeKey[B]] =
    if (typeKey[A].tpe <:< typeOf[HasEmblem])
      Some(typeKey[A].asInstanceOf[TypeKey[B]])
    else
      None

  private def emblemOption[A <: HasEmblem : TypeKey]: Option[A] =
    emblemPool.get(typeKey[A]) map { e => genFromEmblem(e) }

  private def genFromEmblem[A <: HasEmblem](emblem: Emblem[A]): A = {
    val builder = emblem.builder()
    emblem.props.foreach { prop => setEmblemProp(builder, prop) }
    builder.build()
  }

  private def shorthandOption[Long : TypeKey]: Option[Long] =
    shorthandPool.get[Long] map { s => genFromShorthand[Long](s) }

  private def genFromShorthand[Long](shorthand: Shorthand[Long, _]): Long = genFromFullyTypedShorthand(shorthand)

  // http://scabl.blogspot.com/2015/01/introduce-type-param-pattern.html
  private def genFromFullyTypedShorthand[Long, Short](shorthand: Shorthand[Long, Short]): Long =
    basicGenerators.get(shorthand.shortTypeKey) match {
      case Some(gen) => shorthand.unshorten(gen())
      case None => throw new CouldNotGenerateException(shorthand.shortTypeKey)
    }

  // TODO: try to remove code duplication below with optionOption / setOption / listOption
  // generalize to other kinds of "collections"

  private def optionOption[OptionA : TypeKey]: Option[OptionA] = {
    val keyOption = optionTypeKeyOption(typeKey[OptionA])
    keyOption map { k => option(k).asInstanceOf[OptionA] }
  }

  /** returns a `Some` containing the enclosing type of the option whenever the supplied type argument `A`
   * is an Option. otherwise returns `None`. */
  private def optionTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] =
    if (typeKey[A].tpe <:< typeOf[Option[_]]) {
      Some(TypeKey(makeTypeTag(typeKey[A].tpe.typeArgs.head)))
    }
    else None

  private def setOption[SetA : TypeKey]: Option[SetA] = {
    val keyOption = setTypeKeyOption(typeKey[SetA])
    keyOption map { k => set(k).asInstanceOf[SetA] }
  }

  /** returns a `Some` containing the enclosing type of the set whenever the supplied type argument `A`
   * is an Set. otherwise returns `None`. */
  private def setTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] =
    if (typeKey[A].tpe <:< typeOf[Set[_]]) {
      Some(TypeKey(makeTypeTag(typeKey[A].tpe.typeArgs.head)))
    }
    else None

  private def listOption[ListA : TypeKey]: Option[ListA] = {
    val keyOption = listTypeKeyOption(typeKey[ListA])
    keyOption map { k => list(k).asInstanceOf[ListA] }
  }

  /** returns a `Some` containing the enclosing type of the list whenever the supplied type argument `A`
   * is an List. otherwise returns `None`. */
  private def listTypeKeyOption[A : TypeKey]: Option[TypeKey[_]] =
    if (typeKey[A].tpe <:< typeOf[List[_]]) {
      Some(TypeKey(makeTypeTag(typeKey[A].tpe.typeArgs.head)))
    }
    else None

  private def basicOption[Basic : TypeKey]: Option[Basic] = basicGenerators.get[Basic] map { gen => gen() }

  private def setEmblemProp[A <: HasEmblem, B](builder: HasEmblemBuilder[A], prop: EmblemProp[A, B]): Unit =
    builder.setProp(prop, any(prop.typeKey))

  private def isAlphaNumeric(c: Char) =
    (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')

}
