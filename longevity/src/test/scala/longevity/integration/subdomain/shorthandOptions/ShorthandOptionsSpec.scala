package longevity.integration.subdomain.shorthandOptions

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandOptionsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
