package longevity.integration.model.primaryKeyInComponentWithPartialPartition

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PKInComponentWithPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
