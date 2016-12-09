package longevity.integration.model.componentList

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentListSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
