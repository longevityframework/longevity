package longevity.integration.subdomain.controlledVocab

import longevity.subdomain.annotations.derivedComponent
import longevity.subdomain.annotations.polyComponent

@polyComponent
sealed trait ControlledVocab

@derivedComponent[ControlledVocab]
case class VocabTerm1() extends ControlledVocab

@derivedComponent[ControlledVocab]
case object VocabTerm2 extends ControlledVocab
