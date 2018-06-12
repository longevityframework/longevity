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

  /** generates an arbitrary instance of type `A`
   * @param arbitrary the `scalacheck.Arbitrary` that does the actual work
   */
  def generate[A](implicit a: Arbitrary[A]): A

}

private[longevity] object TestDataGenerator {

  def apply[M] = new TestDataGenerator[M] {
    private var seed = Seed.apply(System.nanoTime)
    def generateP[P : PEv[M, ?]]: P = generate(implicitly[PEv[M, P]].arbitrary)
    def generateInt: Int = generate[Int]
    def generateString: String = generate[String]
    def generate[A](implicit arbitrary: Arbitrary[A]): A = synchronized {
      val a = arbitrary.arbitrary.pureApply(Gen.Parameters.default, seed)
      seed = seed.next
      a
    }
  }

}
