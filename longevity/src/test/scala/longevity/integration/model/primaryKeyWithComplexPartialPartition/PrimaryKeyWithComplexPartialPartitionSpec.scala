package longevity.integration.model.primaryKeyWithComplexPartialPartition

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PrimaryKeyWithComplexPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
