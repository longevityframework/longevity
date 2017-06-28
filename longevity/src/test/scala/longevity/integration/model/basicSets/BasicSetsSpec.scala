package longevity.integration.model.basicSets

import org.scalatest.Suites

class BasicSetsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
