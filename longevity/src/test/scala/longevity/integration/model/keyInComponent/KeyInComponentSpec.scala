package longevity.integration.model.keyInComponent

import org.scalatest.Suites

class KeyInComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
