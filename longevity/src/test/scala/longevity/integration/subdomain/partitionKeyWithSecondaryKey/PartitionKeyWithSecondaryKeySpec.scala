package longevity.integration.subdomain.partitionKeyWithSecondaryKey

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PartitionKeyWithSecondaryKeySpec extends Suites(contexts.map(_.repoCrudSpec): _*)

