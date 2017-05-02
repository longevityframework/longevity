package longevity.integration.model.componentList

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithComponentListSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
