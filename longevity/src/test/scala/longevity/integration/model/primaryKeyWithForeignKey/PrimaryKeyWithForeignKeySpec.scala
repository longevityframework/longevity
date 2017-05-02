package longevity.integration.model.primaryKeyWithForeignKey

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class PrimaryKeyWithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)
