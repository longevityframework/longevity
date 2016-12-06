package longevity.test

import emblem.TypeKey
import emblem.emblematic.Emblematic
import emblem.emblematic.traversors.sync.{ CustomGenerator => EmblemCustomGen }
import emblem.emblematic.traversors.sync.{ CustomGeneratorPool => EmblemCustomGenPool }
import emblem.emblematic.traversors.sync.{ Generator => EmblemGen }
import emblem.emblematic.traversors.sync.{ TestDataGenerator => EmblemTestDataGen }
import emblem.exceptions.{ CouldNotGenerateException => EmblemCouldNotGenE }
import longevity.exceptions.test.CouldNotGenerateException

/** generates test data for your subdomain. you can generate any kind of data
 * that occurs in your subdomain by calling [[TestDataGenerator.generate]] with
 * the appropriate type parameter.
 */
trait TestDataGenerator {

  /** generates data for the specified type `A`
   *
   * @tparam A the type of data to generate
   * 
   * @return the generated data
   * 
   * @throws longevity.exceptions.test.CouldNotGenerateException when attempting
   * to generate data for a type that is not in the subdomain
   */
  def generate[A : TypeKey]: A

}

private[longevity] object TestDataGenerator {

  def apply(emblematic: Emblematic, customs: CustomGeneratorPool) = new TestDataGenerator {

    private def phantomGen(emblemGen: EmblemGen) = new TestDataGenerator {
      def generate[A : TypeKey] = emblemGen.generate[A]
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

    val emblemGen = new EmblemTestDataGen(emblematic, emblemCustomGenPool)

    def generate[A : TypeKey]: A = try {
      emblemGen.generate[A]
    } catch {
      case e: EmblemCouldNotGenE => throw new CouldNotGenerateException[A]
    }

  }

}
