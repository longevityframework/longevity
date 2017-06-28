package longevity.integration.model.primaryKey

import org.scalatest.Suites

class PrimaryKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

