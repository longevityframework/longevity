package longevity.integration.model.componentWithForeignKey

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithComponentWithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

