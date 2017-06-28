package longevity.integration.model.component

import org.scalatest.Suites

class WithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
