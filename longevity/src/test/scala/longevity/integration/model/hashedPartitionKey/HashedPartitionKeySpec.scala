package longevity.integration.model.hashedPartitionKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class HashedPartitionKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

