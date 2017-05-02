package longevity.integration.model.basics

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class BasicsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
