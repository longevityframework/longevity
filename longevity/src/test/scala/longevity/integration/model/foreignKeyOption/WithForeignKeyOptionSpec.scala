package longevity.integration.model.foreignKeyOption

import org.scalatest.Suites

class WithForeignKeyOptionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
