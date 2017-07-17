package longevity.test

import typekey.TypeKey
import longevity.emblem.emblematic.Emblematic
import longevity.emblem.emblematic.traversors.sync.{ CustomGenerator => EmblemCustomGen }
import longevity.emblem.emblematic.traversors.sync.{ CustomGeneratorPool => EmblemCustomGenPool }
import longevity.emblem.emblematic.traversors.sync.{ Generator => EmblemGen }
import longevity.emblem.emblematic.traversors.sync.{ TestDataGenerator => EmblemTestDataGen }
import longevity.emblem.exceptions.{ CouldNotGenerateException => EmblemCouldNotGenE }
import longevity.exceptions.test.CouldNotGenerateException

/** generates test data for your domain model. you can generate any kind of data
 * that occurs in your domain model by calling [[TestDataGenerator.generate]] with
 * the appropriate type parameter.
 */
trait TestDataGenerator {

  def next[A : TypeKey]: (TestDataGenerator, A)

  /** generates data for the specified type `A`
   *
   * @tparam A the type of data to generate
   * 
   * @return the generated data
   * 
   * @throws longevity.exceptions.test.CouldNotGenerateException when attempting
   * to generate data for a type that is not in the domain model
   */
  def generate[A : TypeKey]: A = next._2

}

private[longevity] object TestDataGenerator {

  def apply(
    emblematic: Emblematic,
    customs: CustomGeneratorPool,
    seed: Long = System.currentTimeMillis): TestDataGenerator = new TestDataGenerator {

    private def phantomGen(emblemGen: EmblemGen) = new TestDataGenerator {
      self =>
      def next[A : TypeKey] = (self, emblemGen.generate[A])
    }

    private def toEmblemCustomGen[A](customGen: CustomGenerator[A]) = {
      val underlying = { (emblemGen: EmblemGen) => customGen.f(phantomGen(emblemGen)) }
      customGen.genTypeKey -> EmblemCustomGen.simpleGenerator(underlying)(customGen.genTypeKey)
    }

    private val emblemCustomGenPool: EmblemCustomGenPool = {
      customs.seq.foldLeft(EmblemCustomGenPool.empty) {
        case (acc, custom) => acc + toEmblemCustomGen(custom)
      }
    }

    def emblemGen = new EmblemTestDataGen(emblematic, emblemCustomGenPool, seed)

    def next[A : TypeKey]: (TestDataGenerator, A) = try {
      val s = new scala.util.Random(seed).nextLong
      (TestDataGenerator(emblematic, customs, s), emblemGen.generate[A])
    } catch {
      case e: EmblemCouldNotGenE => throw new CouldNotGenerateException[A]
    }

  }

}
