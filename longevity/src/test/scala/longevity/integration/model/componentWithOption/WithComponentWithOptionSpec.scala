package longevity.integration.model.componentWithOption

import org.scalatest.Suites

class WithComponentWithOptionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

