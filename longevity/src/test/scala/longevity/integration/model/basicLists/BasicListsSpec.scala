package longevity.integration.model.basicLists

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class BasicListsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
