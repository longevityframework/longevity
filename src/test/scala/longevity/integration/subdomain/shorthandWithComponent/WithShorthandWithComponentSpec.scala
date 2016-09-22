package longevity.integration.subdomain.shorthandWithComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithShorthandWithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
