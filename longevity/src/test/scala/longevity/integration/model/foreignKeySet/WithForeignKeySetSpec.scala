package longevity.integration.model.foreignKeySet

import org.scalatest.Suites

class WithForeignKeySetSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

