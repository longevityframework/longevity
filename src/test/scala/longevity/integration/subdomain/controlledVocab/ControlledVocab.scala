package longevity.integration.subdomain.controlledVocab

import longevity.subdomain.Embeddable

sealed trait ControlledVocab extends Embeddable

case class VocabTerm1() extends ControlledVocab

case object VocabTerm2 extends ControlledVocab
