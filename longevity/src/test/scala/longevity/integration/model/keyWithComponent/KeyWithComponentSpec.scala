package longevity.integration.model.keyWithComponent

import org.scalatest.Suites

class KeyWithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
