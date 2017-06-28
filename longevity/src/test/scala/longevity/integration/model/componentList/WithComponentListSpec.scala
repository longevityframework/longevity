package longevity.integration.model.componentList

import org.scalatest.Suites

class WithComponentListSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
