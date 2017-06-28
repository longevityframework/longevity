package longevity.integration.model.key

import org.scalatest.Suites

class KeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

