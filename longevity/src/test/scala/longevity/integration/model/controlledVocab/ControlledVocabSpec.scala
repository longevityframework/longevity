package longevity.integration.model.controlledVocab

import org.scalatest.Suites
import scala.concurrent.ExecutionContext.Implicits.global

class ControlledVocabSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
