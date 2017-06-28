package longevity.integration.model.primaryKeyWithMultipleProperties

import org.scalatest.Suites

class PrimaryKeyWithMultiplePropertiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
