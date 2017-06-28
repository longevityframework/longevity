package longevity.integration.model.primaryKeyWithComplexPartialPartition

import org.scalatest.Suites

class PrimaryKeyWithComplexPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
