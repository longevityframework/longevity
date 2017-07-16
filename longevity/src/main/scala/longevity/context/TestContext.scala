package longevity.context

import longevity.config.InMem
import longevity.persistence.Repo
import longevity.test.RepoCrudSpec
import longevity.test.TestDataGenerator

/** the portion of a [[LongevityContext]] that deals with testing
 * 
 * @tparam F the effect
 * @tparam M the model
 */
trait TestContext[F[_], M] {

  /** a repository used for testing, targeting the same persistence strategy as your repo */
  val testRepo: Repo[F, M]

  /** an in-memory repository for this longevity context, for use in testing */
  val inMemTestRepo: Repo[F, M]

  /** a utility class for generating test data for the model type */
  val testDataGenerator: TestDataGenerator[M]

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
     */
    def repoCrudSpec = new RepoCrudSpec(
      longevityContext,
      longevityContext.testRepo,
      longevityContext.config.backEnd)

    /** a simple [[http://www.scalatest.org/ ScalaTest]] spec to test your
     * [[longevity.context.LongevityContext.inMemTestRepo in-memory repo]]. all you have to do is
     * include this value within a ScalaTest suite. for example:
     *
     * {{{
     * val storefrontContext: LongevityContext = ???
     * class StorefrontRepoCrudSpec extends Suites(storefrontContext.inMemTestRepoCrudSpec)
     * }}}
     */
    def inMemRepoCrudSpec = new RepoCrudSpec(longevityContext, longevityContext.inMemTestRepo, InMem)

  }

}
