package longevity.integration.model.componentOption

import org.scalatest.Suites

class WithComponentOptionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
