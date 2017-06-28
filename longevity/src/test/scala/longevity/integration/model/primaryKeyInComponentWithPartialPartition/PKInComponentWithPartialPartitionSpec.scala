package longevity.integration.model.primaryKeyInComponentWithPartialPartition

import org.scalatest.Suites

class PKInComponentWithPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
