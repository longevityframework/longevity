package longevity.integration.subdomain.partitionKeyInComponent

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class PartitionKeyInComponentSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
