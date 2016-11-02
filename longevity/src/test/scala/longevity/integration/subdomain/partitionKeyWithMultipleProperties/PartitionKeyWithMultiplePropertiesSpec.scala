package longevity.integration.subdomain.partitionKeyWithMultipleProperties

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PartitionKeyWithMultiplePropertiesSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
