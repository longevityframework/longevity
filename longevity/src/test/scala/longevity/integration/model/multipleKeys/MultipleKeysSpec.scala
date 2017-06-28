package longevity.integration.model.multipleKeys

import org.scalatest.Suites

class MultipleKeysSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

