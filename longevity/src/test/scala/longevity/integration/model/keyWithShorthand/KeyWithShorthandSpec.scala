package longevity.integration.model.keyWithShorthand

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithShorthandSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
