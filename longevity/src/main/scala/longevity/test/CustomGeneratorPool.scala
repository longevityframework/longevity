package longevity.test

import typekey.TypeKey
import typekey.typeKey

/** a collection of custom generators for [[TestDataGenerator test data generation]] */
class CustomGeneratorPool private (private[test] val seq: Seq[CustomGenerator[_]]) {

  /** adds a custom data generator to the pool */
  def +[A : TypeKey](f: (TestDataGenerator) => A): CustomGeneratorPool =
    new CustomGeneratorPool(seq :+ new CustomGenerator(typeKey[A], f))

  /** adds a custom data generator to the pool */
  def +[A : TypeKey](f: () => A): CustomGeneratorPool = this.+[A]((gen: TestDataGenerator) => f())

}

/** provides a factory method for an empty pool of custom generators */
object CustomGeneratorPool {

  /** an empty pool of custom generators */
  val empty: CustomGeneratorPool = new CustomGeneratorPool(Seq())

}
