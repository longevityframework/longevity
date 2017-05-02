package longevity.integration.model.shorthands

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class ShorthandsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
