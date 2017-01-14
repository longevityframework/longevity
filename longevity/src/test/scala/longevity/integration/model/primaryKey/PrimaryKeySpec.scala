package longevity.integration.model.primaryKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PrimaryKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

