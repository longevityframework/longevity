package longevity.integration.model.shorthandLists

import org.scalatest.Suites

class ShorthandListsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
