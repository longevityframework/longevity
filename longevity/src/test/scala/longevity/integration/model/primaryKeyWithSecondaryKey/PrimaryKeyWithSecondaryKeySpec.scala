package longevity.integration.model.primaryKeyWithSecondaryKey

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class PrimaryKeyWithSecondaryKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

