package longevity.integration.subdomain.componentWithList

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentWithListSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

