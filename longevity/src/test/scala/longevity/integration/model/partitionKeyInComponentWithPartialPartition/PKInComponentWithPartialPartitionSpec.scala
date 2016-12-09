package longevity.integration.model.partitionKeyInComponentWithPartialPartition

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PKInComponentWithPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
