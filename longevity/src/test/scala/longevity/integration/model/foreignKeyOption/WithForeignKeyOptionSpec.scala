package longevity.integration.model.foreignKeyOption

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithForeignKeyOptionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
