package longevity.integration.model.basics

import org.scalatest.Suites

class BasicsSpec extends Suites(repoCrudSpecs(blockingContexts) ++ repoCrudSpecs(futureContexts): _*)
