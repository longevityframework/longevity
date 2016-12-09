package longevity.integration.model.partitionKeyWithSecondaryKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PartitionKeyWithSecondaryKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

