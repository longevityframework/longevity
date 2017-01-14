package longevity.integration.model.primaryKeyWithShorthand

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PrimaryKeyWithShorthandSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
