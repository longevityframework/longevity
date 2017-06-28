package longevity.integration.model.controlledVocab

import org.scalatest.Suites

class ControlledVocabSpec extends Suites(contexts.map(_.repoCrudSpec): _*)
