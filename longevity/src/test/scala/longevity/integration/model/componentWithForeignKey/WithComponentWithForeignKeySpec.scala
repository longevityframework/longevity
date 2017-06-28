package longevity.integration.model.componentWithForeignKey

import org.scalatest.Suites

class WithComponentWithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

