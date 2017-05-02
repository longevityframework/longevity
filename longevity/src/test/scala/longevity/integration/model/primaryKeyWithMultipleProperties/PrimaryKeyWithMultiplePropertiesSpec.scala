package longevity.integration.model.primaryKeyWithMultipleProperties

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class PrimaryKeyWithMultiplePropertiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
