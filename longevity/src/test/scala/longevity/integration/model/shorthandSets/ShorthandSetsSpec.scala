package longevity.integration.model.shorthandSets

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class ShorthandSetsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
