package longevity.integration.model.componentWithSet

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentWithSetSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

