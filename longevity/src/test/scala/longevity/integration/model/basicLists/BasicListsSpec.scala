package longevity.integration.model.basicLists

import org.scalatest.Suites

class BasicListsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
