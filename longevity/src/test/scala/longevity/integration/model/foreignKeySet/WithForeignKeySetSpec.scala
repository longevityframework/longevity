package longevity.integration.model.foreignKeySet

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithForeignKeySetSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

