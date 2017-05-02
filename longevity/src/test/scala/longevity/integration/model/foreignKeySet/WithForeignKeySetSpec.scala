package longevity.integration.model.foreignKeySet

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithForeignKeySetSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

