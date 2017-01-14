package longevity.integration.model.hashedPrimaryKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class HashedPrimaryKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

