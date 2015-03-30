package longevity

import longevity.context.LongevityContext

package object test {

  /** contains [[http://www.scalatest.org/ ScalaTest]] specs for testing various aspects of a
   * [[longevity.context.LongevityContext LongevityContext]].
   *
   * `ScalaTestSpecs` is provided by an implicit conversion from `LongevityContext`,
   * so that ScalaTest can remain an optional dependency for longevity users.
   */
  implicit class ScalaTestSpecs(longevityContext: LongevityContext) {

    /** a simple [[http://www.scalatest.org/ ScalaTest]] spec to test your
     * [[longevity.context.LongevityContext.repoPool repo pool]]. all you have to do is include this value
     * within a ScalaTest suite. for example:
     *
     * {{{
     * import longevity.test.ScalaTestSpecs
     * import org.scalatest.Suites
     * val storefrontContext: LongevityContext = ???
     * class StorefrontRepoPoolSpec extends Suites(storefrontContext.repoPoolSpec)
     * }}}
     */
    val repoPoolSpec = new RepoPoolSpec(
      longevityContext.subdomain,
      longevityContext.shorthandPool,
      longevityContext.customGeneratorPool,
      longevityContext.repoPool,
      Some("(Mongo)"))

    /** a simple [[http://www.scalatest.org/ ScalaTest]] spec to test your
     * [[longevity.context.LongevityContext.inMemRepoPool in-memory repo pool]]. all you have to do is include
     * this value within a ScalaTest suite. for example:
     *
     * {{{
     * import longevity.test.ScalaTestSpecs
     * import org.scalatest.Suites
     * val storefrontContext: LongevityContext = ???
     * class StorefrontRepoPoolSpec extends Suites(storefrontContext.inMemRepoPoolSpec)
     * }}}
     */
    val inMemRepoPoolSpec = new RepoPoolSpec(
      longevityContext.subdomain,
      longevityContext.shorthandPool,
      longevityContext.customGeneratorPool,
      longevityContext.inMemRepoPool,
      Some("(InMem)"))    

  }

}
