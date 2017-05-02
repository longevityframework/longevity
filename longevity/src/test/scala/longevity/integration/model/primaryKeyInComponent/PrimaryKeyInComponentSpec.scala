package longevity.integration.model.primaryKeyInComponent

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class PrimaryKeyInComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
