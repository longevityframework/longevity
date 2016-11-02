package longevity.integration.subdomain.partitionKeyWithShorthand

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PartitionKeyWithShorthandSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
