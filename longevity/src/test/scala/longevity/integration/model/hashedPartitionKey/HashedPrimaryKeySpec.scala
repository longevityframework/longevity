package longevity.integration.model.hashedPrimaryKey

import org.scalatest.Suites

class HashedPrimaryKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

