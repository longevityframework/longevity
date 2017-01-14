package longevity.integration.model.primaryKeyWithForeignKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PrimaryKeyWithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)
