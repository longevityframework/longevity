package longevity.integration.model.derived

import org.scalatest.Suites

class DerivedEntitiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

