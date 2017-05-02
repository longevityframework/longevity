package longevity.integration.model.foreignKey

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)
