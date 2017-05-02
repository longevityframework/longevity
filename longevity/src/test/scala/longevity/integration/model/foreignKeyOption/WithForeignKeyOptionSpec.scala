package longevity.integration.model.foreignKeyOption

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithForeignKeyOptionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
