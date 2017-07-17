package longevity.context

import longevity.config.InMem
import longevity.persistence.Repo
import longevity.test.CustomGeneratorPool
import longevity.test.RepoCrudSpec
import longevity.test.TestDataGenerator

/** the portion of a [[LongevityContext]] that deals with testing
 * 
 * @tparam F the effect
 * @tparam M the model
 */
trait TestContext[F[_], M] {

  /** a collection of custom generators to use when generating test data.
   * defaults to an empty collection
   */
  val customGeneratorPool: CustomGeneratorPool

  /** a repository used for testing, targeting the same persistence strategy as your repo */
  val testRepo: Repo[F, M]

  /** an in-memory repository for this longevity context, for use in testing */
  val inMemTestRepo: Repo[F, M]

  /** a convenient way to create random test data without regard to seeds or state. you can use it like so:
   *
   * {{{
   * val gen = longevityContext.testDataGenerator
   * val user1 = gen().generate[User]
   * val user2 = gen().generate[User]
   * val user3 = gen().generate[User]
   * }}}
   *
   * if you wish to track use a seed or track state in a functional way, please see
   * [[testDataGenerator(seed: Long)]].
   */
  def testDataGenerator: () => TestDataGenerator

  /** a way to create random test data while tracking state in a functional manner. you can use it like so:
   *
   * {{{
   * val seed = 11707L
   * val gen0 = longevityContext.testDataGenerator(seed)
   * val (gen1, user1) = gen0.next[User]
   * val (gen2, user2) = gen1.next[User]
   * val (gen3, user3) = gen2.next[User]
   * }}}
   *
   * if you do not wish to track use a seed or track state in a functional way, please see
   * [[testDataGenerator]].
   */
  def testDataGenerator(seed: Long): TestDataGenerator

}

/** contains an implicit class for ScalaTest methods */
object TestContext {

  /** contains [[http://www.scalatest.org/ ScalaTest]] specs for testing various
   * aspects of a [[longevity.context.LongevityContext LongevityContext]].
   *
   * `ScalaTestSpecs` is provided by an implicit conversion from
   * `LongevityContext`, so that ScalaTest can remain an optional dependency
   * for longevity users. otherwise, it would have been included as part of the
   * [[TestContext]].
   *
   * @tparam F the effect
   * 
   * @tparam M the model
   */
  implicit class ScalaTestSpecs[F[_], M](longevityContext: LongevityContext[F, M]) {

    /** a simple [[http://www.scalatest.org/ ScalaTest]] spec to test your
     * [[longevity.context.LongevityContext.repo repo]]. all you have
     * to do is include this value within a ScalaTest suite. for example:
     *
     * {{{
     * val storefrontContext: LongevityContext = ???
     * class StorefrontRepoCrudSpec extends Suites(storefrontContext.repoCrudSpec)
     * }}}
     *
     * @param seed the seed for random test data generation. defaults to `System.currentTimeMillis`
     */
    def repoCrudSpec(seed: Long = System.currentTimeMillis) = new RepoCrudSpec(
      longevityContext,
      longevityContext.testRepo,
      TestDataGenerator(longevityContext.modelType.emblematic, longevityContext.customGeneratorPool, seed),
      longevityContext.config.backEnd)

    /** a simple [[http://www.scalatest.org/ ScalaTest]] spec to test your
     * [[longevity.context.LongevityContext.inMemTestRepo in-memory repo]]. all you have to do is
     * include this value within a ScalaTest suite. for example:
     *
     * {{{
     * val storefrontContext: LongevityContext = ???
     * class StorefrontRepoCrudSpec extends Suites(storefrontContext.inMemTestRepoCrudSpec)
     * }}}
     *
     * @param seed the seed for random test data generation. defaults to `System.currentTimeMillis`
     */
    def inMemRepoCrudSpec(seed: Long = System.currentTimeMillis) = new RepoCrudSpec(
      longevityContext,
      longevityContext.inMemTestRepo,
      TestDataGenerator(longevityContext.modelType.emblematic, longevityContext.customGeneratorPool, seed),
      InMem)

  }

}
