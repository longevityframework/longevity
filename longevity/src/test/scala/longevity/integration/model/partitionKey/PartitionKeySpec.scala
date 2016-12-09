package longevity.integration.model.partitionKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PartitionKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

