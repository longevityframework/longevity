package longevity.integration.model.foreignKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class WithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)
