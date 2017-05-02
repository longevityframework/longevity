package longevity.integration.model.primaryKeyWithComponent

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class PrimaryKeyWithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
