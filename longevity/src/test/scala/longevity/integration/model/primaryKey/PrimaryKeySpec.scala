package longevity.integration.model.primaryKey

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class PrimaryKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

