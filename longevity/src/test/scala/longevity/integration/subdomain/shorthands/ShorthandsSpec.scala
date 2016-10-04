package longevity.integration.subdomain.shorthands

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
