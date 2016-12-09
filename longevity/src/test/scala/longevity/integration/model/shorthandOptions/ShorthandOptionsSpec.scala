package longevity.integration.model.shorthandOptions

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ShorthandOptionsSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
