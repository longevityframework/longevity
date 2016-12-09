package longevity.integration.model.componentShorthands

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentShorthandsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)


