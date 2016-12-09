package longevity.integration.model.indexWithMultipleProperties

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class IndexWithMultiplePropertiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
