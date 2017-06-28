package longevity.integration.model.keyWithForeignKey

import org.scalatest.Suites

class KeyWithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)
