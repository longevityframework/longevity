package longevity.integration.model.keyWithMultipleProperties

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithMultiplePropertiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
