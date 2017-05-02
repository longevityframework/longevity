package longevity.integration.model.primaryKeyWithShorthand

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class PrimaryKeyWithShorthandSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
