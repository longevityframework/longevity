package longevity.integration.model.hashedPrimaryKey

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class HashedPrimaryKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

