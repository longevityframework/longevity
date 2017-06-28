package longevity.integration.model.basics

import org.scalatest.Suites

class BasicsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
