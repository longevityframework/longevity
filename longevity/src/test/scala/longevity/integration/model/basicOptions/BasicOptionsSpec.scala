package longevity.integration.model.basicOptions

import org.scalatest.Suites

class BasicOptionsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
