package longevity.context

import emblem.traversors.sync.Generator.CustomGeneratorPool
import longevity.persistence.RepoPool
import longevity.test.RepoPoolSpec

/** the portion of a [[LongevityContext]] that deals with testing */
trait TestContext {

  /** a collection of custom generators to use when generating test data. defaults to an empty collection */
  val customGeneratorPool: CustomGeneratorPool

  /** a set of repositories used for testing, targeting the same persistence strategy as your main repo pool */
  val testRepoPool: RepoPool

  /** an in-memory set of repositories for this longevity context, for use in testing */
  val inMemTestRepoPool: RepoPool

}

object TestContext {

  /** contains [[http://www.scalatest.org/ ScalaTest]] specs for testing various aspects of a
   * [[longevity.context.LongevityContext LongevityContext]].
   *
   * `ScalaTestSpecs` is provided by an implicit conversion from `LongevityContext`,
   * so that ScalaTest can remain an optional dependency for longevity users. otherwise, it would have been
   * included as part of the [[TestContext]].
   */
  implicit class ScalaTestSpecs(longevityContext: LongevityContext) {

    /** a simple [[http://www.scalatest.org/ ScalaTest]] spec to test your
     * [[longevity.context.LongevityContext.repoPool repo pool]]. all you have to do is include this value
     * within a ScalaTest suite. for example:
     *
     * {{{
     * val storefrontContext: LongevityContext = ???
     * class StorefrontRepoPoolSpec extends Suites(storefrontContext.repoPoolSpec)
     * }}}
     */
    val repoPoolSpec = new RepoPoolSpec(
      longevityContext.subdomain,
      longevityContext.customGeneratorPool,
      longevityContext.testRepoPool,
      Some("(Mongo)"))

    /** a simple [[http://www.scalatest.org/ ScalaTest]] spec to test your
     * [[longevity.context.LongevityContext.inMemRepoPool in-memory repo pool]]. all you have to do is include
     * this value within a ScalaTest suite. for example:
     *
     * {{{
     * val storefrontContext: LongevityContext = ???
     * class StorefrontRepoPoolSpec extends Suites(storefrontContext.inMemRepoPoolSpec)
     * }}}
     */
    val inMemRepoPoolSpec = new RepoPoolSpec(
      longevityContext.subdomain,
      longevityContext.customGeneratorPool,
      longevityContext.inMemTestRepoPool,
      Some("(InMem)"))    

  }

}
