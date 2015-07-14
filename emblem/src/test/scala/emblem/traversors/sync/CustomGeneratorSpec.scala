package emblem.traversors.sync

import emblem.imports._
import emblem.testData.emblems._
import emblem.testData.extractors._
import emblem.traversors.sync.CustomGenerator.simpleGenerator
import emblem.traversors.sync.Generator._
import org.scalatest._

/** specs for [[CustomGenerator]] */
class CustomGeneratorSpec extends FlatSpec with GivenWhenThen with Matchers {

  class IntHolder(val i: Int)

  behavior of "the example code in the scaladocs for CustomGenerator.simpleGenerator"
  it should "compile and produce the expected results" in {

    val intHolderGen: CustomGenerator[IntHolder] =
      simpleGenerator((generator: Generator) => new IntHolder(generator.generate[Int]))
    val generator = new TestDataGenerator(customGeneratorPool = CustomGeneratorPool.empty + intHolderGen)

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
    val generator = new TestDataGenerator(customGeneratorPool = CustomGeneratorPool.empty + listCustomGenerator)

  }

}
