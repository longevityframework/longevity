package longevity.integration.model.partitionKeyWithComplexPartialPartition

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PartitionKeyWithComplexPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
