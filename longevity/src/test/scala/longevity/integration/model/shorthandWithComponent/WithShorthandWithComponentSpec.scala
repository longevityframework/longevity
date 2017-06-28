package longevity.integration.model.shorthandWithComponent

import org.scalatest.Suites

class WithShorthandWithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
