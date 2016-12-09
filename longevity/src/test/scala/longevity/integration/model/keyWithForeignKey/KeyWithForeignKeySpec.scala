package longevity.integration.model.keyWithForeignKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class KeyWithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)
