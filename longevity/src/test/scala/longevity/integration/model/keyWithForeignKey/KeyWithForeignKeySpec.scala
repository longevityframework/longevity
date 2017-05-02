package longevity.integration.model.keyWithForeignKey

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class KeyWithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)
