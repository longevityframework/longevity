package longevity.integration.model.primaryKeyInComponentWithPartialPartition

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class PKInComponentWithPartialPartitionSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
