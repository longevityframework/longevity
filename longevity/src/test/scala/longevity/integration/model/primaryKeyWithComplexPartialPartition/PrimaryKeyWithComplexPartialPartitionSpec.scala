package longevity.integration.model.primaryKeyWithComplexPartialPartition

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class PrimaryKeyWithComplexPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
