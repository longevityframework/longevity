package longevity.integration.model.indexWithMultipleProperties

import org.scalatest.Suites

class IndexWithMultiplePropertiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
