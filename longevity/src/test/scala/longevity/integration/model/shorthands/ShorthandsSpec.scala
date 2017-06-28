package longevity.integration.model.shorthands

import org.scalatest.Suites

class ShorthandsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
