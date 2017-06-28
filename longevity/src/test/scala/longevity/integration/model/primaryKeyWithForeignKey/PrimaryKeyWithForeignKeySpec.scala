package longevity.integration.model.primaryKeyWithForeignKey

import org.scalatest.Suites

class PrimaryKeyWithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)
