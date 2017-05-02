package longevity.integration.model.keyWithComponent

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class KeyWithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
