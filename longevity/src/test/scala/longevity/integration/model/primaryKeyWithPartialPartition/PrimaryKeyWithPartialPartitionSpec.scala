package longevity.integration.model.primaryKeyWithPartialPartition

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PrimaryKeyWithPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
