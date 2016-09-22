package longevity.integration.subdomain.basicSets

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class BasicSetsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
