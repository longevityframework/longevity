package longevity.unit.manual

import org.scalatest._

object ContextSpec {

}

/** exercises code samples found in the context section of the user manual. the samples themselves are
 * in [[RootTypeSpec]] companion object. we include them in the tests here to force the initialization of the
 * subdomains, and to perform some basic sanity checks on the results.
 *
 * @see http://longevityframework.github.io/longevity/manual/context
 */
class ContextSpec extends FlatSpec with GivenWhenThen with Matchers {

  "user manual example code" should "produce correct queries" in {


  }

}
