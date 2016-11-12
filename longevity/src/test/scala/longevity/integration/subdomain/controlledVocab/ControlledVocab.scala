package longevity.integration.subdomain.controlledVocab


sealed trait ControlledVocab

case class VocabTerm1() extends ControlledVocab

case object VocabTerm2 extends ControlledVocab
