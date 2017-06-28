package longevity.integration.model.componentShorthands

import org.scalatest.Suites

class WithComponentShorthandsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)


