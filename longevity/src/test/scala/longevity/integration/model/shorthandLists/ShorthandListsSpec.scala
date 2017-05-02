package longevity.integration.model.shorthandLists

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class ShorthandListsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
