package longevity.integration.model.componentSet

import org.scalatest.Suites

class WithComponentSetSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
