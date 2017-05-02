package longevity.integration.model.controlledVocab

import org.scalatest.Suites
import longevity.integration.model.modelTestsExecutionContext

class ControlledVocabSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
