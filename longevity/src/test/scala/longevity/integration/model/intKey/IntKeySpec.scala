package longevity.integration.model.intKey

import org.scalatest.Suites

class IntKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

