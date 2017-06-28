package longevity.integration.model.primaryKeyWithShorthand

import org.scalatest.Suites

class PrimaryKeyWithShorthandSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
