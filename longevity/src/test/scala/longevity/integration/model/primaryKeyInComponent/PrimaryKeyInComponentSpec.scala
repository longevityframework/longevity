package longevity.integration.model.primaryKeyInComponent

import org.scalatest.Suites

class PrimaryKeyInComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
