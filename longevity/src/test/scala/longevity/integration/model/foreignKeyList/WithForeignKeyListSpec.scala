package longevity.integration.model.foreignKeyList

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithForeignKeyListSpec extends Suites(contexts.map(_.repoCrudSpec): _*)

