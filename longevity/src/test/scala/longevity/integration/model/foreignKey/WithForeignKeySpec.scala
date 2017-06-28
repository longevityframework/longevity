package longevity.integration.model.foreignKey

import org.scalatest.Suites

class WithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)
