package longevity.integration.model.partitionKeyWithComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PartitionKeyWithComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
