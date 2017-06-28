package longevity.integration.model.primaryKeyWithPartialPartition

import org.scalatest.Suites

class PrimaryKeyWithPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
