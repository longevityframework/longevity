package longevity.integration.model.derived

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class DerivedEntitiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

