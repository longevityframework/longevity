package longevity.integration.subdomain.controlledVocab

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ControlledVocabSpec extends Suites(
  context.mongoContext.inMemRepoCrudSpec,
  context.mongoContext.repoCrudSpec,
  context.cassandraContext.repoCrudSpec)
