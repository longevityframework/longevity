package longevity.testUtil

import org.scalatest._
import org.scalatest.OptionValues._
import emblem._
import longevity.exceptions.CouldNotGenerateException
import TestDataGenerator.GeneratorFunction
import emblems._
import shorthands._

/** specs for [[TestDataGenerator]] */
class TestDataGeneratorSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TestDataGenerator.custom[A]"

  class IntHolder(val i: Int)
  class IntHolderNoCustom(val i: Int)

  it should "produce values according to the supplied custom generator" in {
    val intHolderGeneratorFunction = (generator: TestDataGenerator) => new IntHolder(generator.int)
    val customGenerators = TypeKeyMap[Any, GeneratorFunction]() + intHolderGeneratorFunction

    val generator = new TestDataGenerator(
      shorthands.pool,
      emblems.pool,
      customGenerators
    )

    val intHolder: IntHolder = generator.custom[IntHolder]
    List.fill(100) {
      (generator.custom[IntHolder].i) shouldNot equal (generator.custom[IntHolder].i)
    }
  }

  it should "throw CouldNotGenerateException when there is no custom generator for the requested type" in {
    val intHolderGeneratorFunction = (generator: TestDataGenerator) => new IntHolder(generator.int)
    val customGenerators = TypeKeyMap[Any, GeneratorFunction]() + intHolderGeneratorFunction

    val generator = new TestDataGenerator(
      shorthands.pool,
      emblems.pool,
      customGenerators
    )

    intercept[CouldNotGenerateException] { generator.custom[IntHolderNoCustom] }
  }

  behavior of "TestDataGenerator.emblem[A <: HasEmblem]"

  // TODO: specs for when entity props are emblems or customs

  it should "produce random values of type A" in {
    val generator = new TestDataGenerator(shorthands.pool, emblems.pool)

    val point: Point = generator.emblem[Point]
    List.fill(100) {
      (generator.emblem[Point]) shouldNot equal (generator.emblem[Point])
    }

    val user: User = generator.emblem[User]
    List.fill(100) {
      (generator.emblem[User]) shouldNot equal (generator.emblem[User])
    }
  }

  it should "throw CouldNotGenerateException when it does not know have an emblem for the requested type" in {
    val generator = new TestDataGenerator(shorthands.pool, emblems.pool)

    intercept[CouldNotGenerateException] {
      generator.emblem[NotInPool]
    }
  }

  it should "throw CouldNotGenerateException when it does not know how to generate for a property type" in {
    val generator = new TestDataGenerator(shorthands.pool, emblems.pool)

    intercept[CouldNotGenerateException] {
      generator.emblem[WithNoShorthandProp]
    }

    intercept[CouldNotGenerateException] {
      generator.emblem[WithBarProp]
    }
  }

  behavior of "TestDataGenerator.shorthand[Long]"

  // TODO: specs for when Long is an emblem or a custom

  it should "produce random values of type Long when the short type is a basic type" in {
    val generator = new TestDataGenerator(shorthands.pool)

    val email: Email = generator.shorthand[Email]
    List.fill(100) {
      (generator.shorthand[Email]) shouldNot equal (generator.shorthand[Email])
    }

    val radians: Radians = generator.shorthand[Radians]
    List.fill(100) {
      (generator.shorthand[Radians]) shouldNot equal (generator.shorthand[Radians])
    }

    val zipcode: Zipcode = generator.shorthand[Zipcode]
    List.fill(100) {
      (generator.shorthand[Zipcode]) shouldNot equal (generator.shorthand[Zipcode])
    }
  }

  it should "throw CouldNotGenerateException when it does not have a shorthand registered for the Long type" in {
    val generator = new TestDataGenerator()
    intercept[CouldNotGenerateException] {
      generator.shorthand[NoShorthand]
    }
  }

  it should "throw CouldNotGenerateException when it does not know how to generate for shorthand type" in {
    val generator = new TestDataGenerator()
    intercept[CouldNotGenerateException] {
      generator.shorthand[Bar]
    }
  }

  behavior of "TestDataGenerator methods for basic types"
  it should "produce random values for basic, leafy types" in {
    val generator = new TestDataGenerator()

    val b: Boolean = generator.boolean
    val c: Char = generator.char

    val d: Double = generator.double
    List.fill(100) { (generator.double) shouldNot equal (generator.double) }

    val f: Float = generator.float
    List.fill(100) { (generator.float) shouldNot equal (generator.float) }

    val l: Long = generator.long
    List.fill(100) { (generator.long) shouldNot equal (generator.long) }

    val i: Int = generator.int
    List.fill(100) { (generator.int) shouldNot equal (generator.int) }

    def isAlphaNumeric(c: Char) =
    (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')

    (0 to 100) foreach { i =>
      val s: String = generator.string(i)
      s.length should equal (i)
      if (i > 10) {
        s.filterNot(isAlphaNumeric(_)) should equal ("")
        generator.string(i) shouldNot equal (generator.string(i))
      }
    }

    List.fill(100) {
      val s: String = generator.string
      s.length should equal (8)
      s.filterNot(isAlphaNumeric(_)) should equal ("")
    }
  }

  behavior of "TestDataGenerator.custom[A]"
  it should "delegate appropriately as in all the above examples" in {
    val intHolderGeneratorFunction = (generator: TestDataGenerator) => new IntHolder(generator.int)
    val customGenerators = TypeKeyMap[Any, GeneratorFunction]() + intHolderGeneratorFunction

    val generator = new TestDataGenerator(
      shorthands.pool,
      emblems.pool,
      customGenerators
    )

    // customs
    val intHolder: IntHolder = generator.any[IntHolder]
    intercept[CouldNotGenerateException] { generator.any[IntHolderNoCustom] }

    // emblems
    val point: Point = generator.any[Point]
    val user: User = generator.any[User]
    intercept[CouldNotGenerateException] { generator.any[NotInPool] }
    intercept[CouldNotGenerateException] { generator.any[WithNoShorthandProp] }
    intercept[CouldNotGenerateException] { generator.any[WithBarProp] }

    // shorthands
    val email: Email = generator.shorthand[Email]
    val radians: Radians = generator.shorthand[Radians]
    val zipcode: Zipcode = generator.shorthand[Zipcode]
    intercept[CouldNotGenerateException] { generator.shorthand[NoShorthand] }
    intercept[CouldNotGenerateException] { generator.shorthand[Bar] }

    // basics
    val b: Boolean = generator.any[Boolean]
    val c: Char = generator.any[Char]
    val d: Double = generator.any[Double]
    val f: Float = generator.any[Float]
    val l: Long = generator.any[Long]
    val i: Int = generator.any[Int]
    val s: String = generator.any[String]

  }

}
