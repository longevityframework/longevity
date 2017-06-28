package longevity.integration.model.primaryKeyWithSecondaryKey

import org.scalatest.Suites

class PrimaryKeyWithSecondaryKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

