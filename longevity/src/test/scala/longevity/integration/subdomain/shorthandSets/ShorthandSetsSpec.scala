package longevity.integration.subdomain.shorthandSets

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandSetsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
