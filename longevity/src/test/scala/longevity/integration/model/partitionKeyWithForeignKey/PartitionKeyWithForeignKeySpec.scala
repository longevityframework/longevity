package longevity.integration.model.partitionKeyWithForeignKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PartitionKeyWithForeignKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)
