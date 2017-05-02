package longevity.integration.model.foreignKeyList

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class WithForeignKeyListSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

