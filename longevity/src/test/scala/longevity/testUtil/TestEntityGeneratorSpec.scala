package longevity.testUtil

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.exceptions.CouldNotGenerateException

/** specs for [[TestDataGenerator]] */
class TestDataGeneratorSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TestDataGenerator.emblem(emblem)"

  it should "produce random emblem values" in {
    import emblems._
    val generator = new TestDataGenerator(shorthands.pool)

    val point: Point = generator.emblem(pointEmblem)
    List.fill(100) {
      (generator.emblem(pointEmblem)) shouldNot equal (generator.emblem(pointEmblem))
    }

    val user: User = generator.emblem(userEmblem)
    List.fill(100) {
      (generator.emblem(userEmblem)) shouldNot equal (generator.emblem(userEmblem))
    }
  }

  it should "throw CouldNotGenerateException when it does not know how to generate for a property type" in {
    import emblems._
    val generator = new TestDataGenerator(shorthands.pool)

    intercept[CouldNotGenerateException] {
      generator.emblem(withNoShorthandPropEmblem)
    }

    intercept[CouldNotGenerateException] {
      generator.emblem(withBarPropEmblem)
    }
  }

  behavior of "TestDataGenerator.shorthand[Long]"

  it should "produce random shorthand values when the short type is a basic type" in {
    import shorthands._
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
    import shorthands._
    val generator = new TestDataGenerator()
    intercept[CouldNotGenerateException] {
      generator.shorthand[NoShorthand]
    }
  }

  it should "throw CouldNotGenerateException when it does not know how to generate for shorthand type" in {
    import shorthands._
    val generator = new TestDataGenerator()
    intercept[CouldNotGenerateException] {
      generator.shorthand[Bar]
    }
  }

  behavior of "TestDataGenerator.shorthand(Shorthand)"

  it should "produce random shorthand values when the short type is a basic type" in {
    import shorthands._
    val generator = new TestDataGenerator()

    val email: Email = generator.shorthand(emailShorthand)
    List.fill(100) {
      (generator.shorthand(emailShorthand)) shouldNot equal (generator.shorthand(emailShorthand))
    }

    val radians: Radians = generator.shorthand(radiansShorthand)
    List.fill(100) {
      (generator.shorthand(radiansShorthand)) shouldNot equal (generator.shorthand(radiansShorthand))
    }

    val zipcode: Zipcode = generator.shorthand(zipcodeShorthand)
    List.fill(100) {
      (generator.shorthand(zipcodeShorthand)) shouldNot equal (generator.shorthand(zipcodeShorthand))
    }
  }

  it should "throw CouldNotGenerateException when it does not know how to generate for shorthand type" in {
    import shorthands._
    val generator = new TestDataGenerator()
    intercept[CouldNotGenerateException] {
      generator.shorthand(barShorthand)
    }
  }

  behavior of "TestDataGenerator methods for basic types"
  it should "produce random values for basic, leafy types" in {
    val generator = new TestDataGenerator()

    val b: Boolean = generator.boolean()
    val c: Char = generator.char()

    val d: Double = generator.double()
    List.fill(100) { (generator.double()) shouldNot equal (generator.double()) }

    val f: Float = generator.float()
    List.fill(100) { (generator.float()) shouldNot equal (generator.float()) }

    val l: Long = generator.long()
    List.fill(100) { (generator.long()) shouldNot equal (generator.long()) }

    val i: Int = generator.int()
    List.fill(100) { (generator.int()) shouldNot equal (generator.int()) }

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
      val s: String = generator.string()
      s.length should equal (8)
      s.filterNot(isAlphaNumeric(_)) should equal ("")
    }  

  }

}
