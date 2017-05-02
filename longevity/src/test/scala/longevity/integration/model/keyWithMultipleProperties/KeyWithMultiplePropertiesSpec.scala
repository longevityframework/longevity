package longevity.integration.model.keyWithMultipleProperties

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class KeyWithMultiplePropertiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
