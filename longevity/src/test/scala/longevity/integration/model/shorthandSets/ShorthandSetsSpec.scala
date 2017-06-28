package longevity.integration.model.shorthandSets

import org.scalatest.Suites

class ShorthandSetsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
