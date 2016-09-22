package longevity.integration.subdomain.componentSet

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentSetSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
