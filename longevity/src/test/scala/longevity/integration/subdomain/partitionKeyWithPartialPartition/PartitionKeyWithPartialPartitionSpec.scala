package longevity.integration.subdomain.partitionKeyWithPartialPartition

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PartitionKeyWithPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
