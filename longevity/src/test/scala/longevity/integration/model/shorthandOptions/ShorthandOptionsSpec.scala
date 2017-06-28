package longevity.integration.model.shorthandOptions

import org.scalatest.Suites

class ShorthandOptionsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
