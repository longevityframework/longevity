package longevity.integration.model.componentWithForeignKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithComponentWithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

