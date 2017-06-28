package longevity.integration.model.keyWithMultipleProperties

import org.scalatest.Suites

class KeyWithMultiplePropertiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
