package longevity.integration.model.foreignKeyList

import org.scalatest.Suites

class WithForeignKeyListSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

