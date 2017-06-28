package longevity.integration.model.primaryKeyWithComponent

import org.scalatest.Suites

class PrimaryKeyWithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
