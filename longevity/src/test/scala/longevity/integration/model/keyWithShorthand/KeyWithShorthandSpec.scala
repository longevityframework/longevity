package longevity.integration.model.keyWithShorthand

import org.scalatest.Suites

class KeyWithShorthandSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
