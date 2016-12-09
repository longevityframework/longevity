package longevity.integration.model.keyInComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyInComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
