package longevity.integration.model.key

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

