package longevity.test

import longevity.model.PEv
import org.scalacheck.Arbitrary
import org.scalacheck.Gen
import org.scalacheck.rng.Seed

/** generates test data for your domain model. you can generate any persistent that occurs in your
 * domain model by calling [[TestDataGenerator.generateP]] with the appropriate type parameter.
 */
trait TestDataGenerator[M] {

  /** generates a random persistent object of type `P` in domain model `M` */
  def generateP[P : PEv[M, ?]]: P

  /** generates a random integer */
  def generateInt: Int

  /** generates a random string */
  def generateString: String

}

private[longevity] object TestDataGenerator {

  private var seed = Seed.apply(System.currentTimeMillis)

  def apply[M] = new TestDataGenerator[M] {

    def generateP[P : PEv[M, ?]]: P = {
      val p = implicitly[PEv[M, P]].arbitrary.arbitrary.pureApply(Gen.Parameters.default, seed)
      seed = seed.next
      p
    }

    def generateInt: Int = {
      val i = implicitly[Arbitrary[Int]].arbitrary.pureApply(Gen.Parameters.default, seed)
      seed = seed.next
      i
    }

    def generateString: String = {
      val s = implicitly[Arbitrary[String]].arbitrary.pureApply(Gen.Parameters.default, seed)
      seed = seed.next
      s
    }

  }

}
