package emblem.traversors.sync

import emblem.imports._
import emblem.exceptions.CouldNotGenerateException
import emblem.testData.emblems._
import emblem.testData.extractors._
import emblem.traversors.sync.CustomGenerator.simpleGenerator
import emblem.traversors.sync.Generator._
import org.scalatest.OptionValues._
import org.scalatest._

/** specs for [[TestDataGenerator]] */
class TestDataGeneratorSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "TestDataGenerator.generate[A] with custom generator"

  it should "produce values according to the supplied custom generator" in {
    val generator = standardGenerator
    val intHolder: IntHolder = generator.generate[IntHolder]
    List.fill(10) {
      (generator.generate[IntHolder].i) shouldNot equal (generator.generate[IntHolder].i)
    }
  }

  it should "throw CouldNotGenerateException when there is no custom generator for the requested type" in {
    val generator = standardGenerator
    intercept[CouldNotGenerateException] { generator.generate[IntHolderNoCustom] }
  }

  it should "work properly with generators for types that take type params" in {
    val intHolderCustomGenerator =
      simpleGenerator((generator: Generator) => new IntHolder(generator.generate[Int]))

    // always generates a 5-element list
    val listCustomGenerator = new CustomGenerator[List[Any]] {
      def apply[B <: List[_] : TypeKey](generator: Generator): B = {
        val eltTypeKey = typeKey[B].typeArgs.head
        val eltList = List.fill(5) { generator.generate(eltTypeKey) }
        eltList.asInstanceOf[B]
      }
    }

    val customGeneratorPool = CustomGeneratorPool.empty + intHolderCustomGenerator + listCustomGenerator
    val generator = new TestDataGenerator(emblemPool, extractorPool, customGeneratorPool)

    List.fill(10) {
      val intList: List[Int] = generator.generate[List[Int]]
      intList.size should equal (5)

      val stringList: List[String] = generator.generate[List[String]]
      stringList.size should equal (5)

      val pointList: List[Point] = generator.generate[List[Point]]
      pointList.size should equal (5)
    }

    // flotsam
    intercept[CouldNotGenerateException] { generator.generate[List[_]] }
  }

  behavior of "TestDataGenerator.generate[A] for A <:< HasEmblem"

  it should "produce random values of type A" in {
    val generator = standardGenerator

    val point: Point = generator.generate[Point]
    List.fill(10) {
      (generator.generate[Point]) shouldNot equal (generator.generate[Point])
    }

    val friend: Friend = generator.generate[Friend]
    List.fill(10) {
      (generator.generate[Friend]) shouldNot equal (generator.generate[Friend])
    }
  }

  it should "throw CouldNotGenerateException when it does not know have an emblem for the requested type" in {
    val generator = standardGenerator
    intercept[CouldNotGenerateException] { generator.generate[NotInPool] }
  }

  it should "throw CouldNotGenerateException when it does not know how to generate for a property type" in {
    val generator = standardGenerator
    intercept[CouldNotGenerateException] { generator.generate[WithNoExtractorProp] }
    intercept[CouldNotGenerateException] { generator.generate[WithBarProp] }
  }

  behavior of "TestDataGenerator.generate[A] when A has a extractor"

  it should "produce random values of type Long when the short type is a basic type" in {
    val generator = standardGenerator

    val email: Email = generator.generate[Email]
    List.fill(10) {
      (generator.generate[Email]) shouldNot equal (generator.generate[Email])
    }

    val radians: Radians = generator.generate[Radians]
    List.fill(10) {
      (generator.generate[Radians]) shouldNot equal (generator.generate[Radians])
    }

    val zipcode: Zipcode = generator.generate[Zipcode]
    List.fill(10) {
      (generator.generate[Zipcode]) shouldNot equal (generator.generate[Zipcode])
    }
  }

  it should "throw CouldNotGenerateException when it does not have a extractor registered for the Long type" in {
    val generator = standardGenerator
    intercept[CouldNotGenerateException] { generator.generate[NoExtractor] }
  }

  it should "throw CouldNotGenerateException when it does not know how to generate for extractor type" in {
    val generator = standardGenerator
    intercept[CouldNotGenerateException] { generator.generate[Bar] }
  }

  behavior of "TestDataGenerator.generate[Option[A]]"
  it should "produce an option that is None about half the time" in {
    val generator = standardGenerator

    val stringOption: Option[String] = generator.generate[Option[String]]
    val stringOptionList = List.fill(100) { generator.generate[Option[String]] }
    val someCount = stringOptionList.flatten.size
    someCount should be > 20
    someCount should be < 80

    val pointOption: Option[Point] = generator.generate[Option[Point]]
    val emailOption: Option[Email] = generator.generate[Option[Email]]
  }

  behavior of "TestDataGenerator.generate[Set[A]]"
  it should "produce a set that has 0, 1, and 2 elements at least some of the time" in {
    val generator = standardGenerator

    val stringSet: Set[String] = generator.generate[Set[String]]
    val stringSetList: List[Set[String]] = List.fill(50) { generator.generate[Set[String]] }
    val sizeToStringSetMap = stringSetList.groupBy(_.size)
    sizeToStringSetMap.get(0).value.size should be > 1
    sizeToStringSetMap.get(1).value.size should be > 1
    sizeToStringSetMap.get(2).value.size should be > 1

    val pointSet: Set[Point] = generator.generate[Set[Point]]
    val emailSet: Set[Email] = generator.generate[Set[Email]]
  }

  behavior of "TestDataGenerator.generate[List[A]]"
  it should "produce a list that has 0, 1, and 2 elements at least some of the time" in {
    val generator = standardGenerator

    val stringList: List[String] = generator.generate[List[String]]
    val stringListList = List.fill(50) { generator.generate[List[String]] }
    val sizeToStringListMap = stringListList.groupBy(_.size)
    sizeToStringListMap.get(0).value.size should be > 1
    sizeToStringListMap.get(1).value.size should be > 1
    sizeToStringListMap.get(2).value.size should be > 1

    val pointList: List[Point] = generator.generate[List[Point]]
    val emailList: List[Email] = generator.generate[List[Email]]
  }

  behavior of "TestDataGenerator methods for basic types"
  it should "produce random values for basic, leafy types" in {
    val generator = standardGenerator

    val b: Boolean = generator.boolean
    val c: Char = generator.char

    val d: Double = generator.double
    List.fill(10) { (generator.double) shouldNot equal (generator.double) }

    val f: Float = generator.float
    List.fill(10) { (generator.float) shouldNot equal (generator.float) }

    val l: Long = generator.long
    List.fill(10) { (generator.long) shouldNot equal (generator.long) }

    val i: Int = generator.int
    List.fill(10) { (generator.int) shouldNot equal (generator.int) }

    def isAlphaNumeric(c: Char) =
    (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')

    (0 to 10) foreach { i =>
      val s: String = generator.string(i)
      s.length should equal (i)
      s.filterNot(isAlphaNumeric(_)) should equal ("")
      if (i > 5) {
        generator.string(i) shouldNot equal (generator.string(i))
      }
    }

    List.fill(10) {
      val s: String = generator.string
      s.length should equal (8)
      s.filterNot(isAlphaNumeric(_)) should equal ("")
    }
  }

  it should "give precedence to customs over emblems, extractors, collections, and basics" in {
    val uriCustomGenerator = simpleGenerator((generator) => Uri("frenchy"))
    val pointCustomGenerator = simpleGenerator((generator) => Point(-1.0, -1.0))
    val listCustomGenerator = simpleGenerator((generator) => List(1, 2, 3))
    val intCustomGenerator = simpleGenerator((generator) => 77)
    val customGeneratorPool = CustomGeneratorPool.empty +
      uriCustomGenerator + pointCustomGenerator + listCustomGenerator + intCustomGenerator

    val generator = new TestDataGenerator(emblemPool, extractorPool, customGeneratorPool)

    generator.generate[Uri] should equal (Uri("frenchy"))
    generator.generate[Point] should equal (Point(-1.0, -1.0))
    generator.generate[List[Int]] should equal (List(1, 2, 3))
    generator.generate[Int] should equal (77)
  }

  class IntHolder(val i: Int)
  class IntHolderNoCustom(val i: Int)

  private def standardGenerator = {
    val intHolderCustomGenerator = simpleGenerator(
      (generator: Generator) => new IntHolder(generator.generate[Int]))
    val customGeneratorPool = CustomGeneratorPool.empty + intHolderCustomGenerator
    new TestDataGenerator(
      emblemPool = emblemPool,
      extractorPool = extractorPool,
      customGeneratorPool = CustomGeneratorPool.empty + intHolderCustomGenerator)
  }

}
