package longevity.integration.model.basicSets

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class BasicSetsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
