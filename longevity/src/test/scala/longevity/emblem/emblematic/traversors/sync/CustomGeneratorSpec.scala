package longevity.emblem.emblematic.traversors.sync

import typekey.TypeKey
import typekey.typeKey
import longevity.emblem.emblematic.traversors.sync.CustomGenerator.simpleGenerator
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specs for [[CustomGenerator]] */
class CustomGeneratorSpec extends FlatSpec with GivenWhenThen with Matchers {

  class IntHolder(val i: Int)

  behavior of "the example code in the scaladocs for CustomGenerator.simpleGenerator"
  it should "compile and produce the expected results" in {

    var callCount = 0
    val intHolderGen: CustomGenerator[IntHolder] =
      simpleGenerator((generator: Generator) => {
        callCount += 1
        new IntHolder(generator.generate[Int])
      })
    val generator = new TestDataGenerator(
      customGeneratorPool = CustomGeneratorPool.empty + intHolderGen)

    generator.generate[Int]
    callCount should equal (0)

    generator.generate[IntHolder]
    callCount should equal (1)

    generator.generate[IntHolder]
    callCount should equal (2)
  }

  behavior of "the example code in the scaladocs for trait CustomGenerator"
  it should "compile and produce the expected results" in {

    // always generates a 5-element list
    val listCustomGenerator = new CustomGenerator[List[Any]] {
      def apply[B <: List[_] : TypeKey](generator: Generator): B = {
        val eltTypeKey = typeKey[B].typeArgs.head
        val eltList = List.fill(5) { generator.generate(eltTypeKey) }
        eltList.asInstanceOf[B]
      }
    }
    val generator = new TestDataGenerator(
      customGeneratorPool = CustomGeneratorPool.empty + listCustomGenerator)

    generator.generate[List[Int]].size should equal (5)
    generator.generate[List[String]].size should equal (5)
    generator.generate[List[Option[Double]]].size should equal (5)
  }

}
