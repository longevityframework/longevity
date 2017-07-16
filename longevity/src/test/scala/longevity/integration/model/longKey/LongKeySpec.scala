package longevity.integration.model.longKey

import org.scalatest.Suites

class LongKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

