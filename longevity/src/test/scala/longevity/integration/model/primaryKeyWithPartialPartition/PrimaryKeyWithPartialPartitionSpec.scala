package longevity.integration.model.primaryKeyWithPartialPartition

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class PrimaryKeyWithPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
