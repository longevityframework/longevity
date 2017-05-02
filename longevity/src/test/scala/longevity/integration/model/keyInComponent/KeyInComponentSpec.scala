package longevity.integration.model.keyInComponent

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class KeyInComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
