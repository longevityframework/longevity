package longevity.integration.model.controlledVocab

import longevity.model.annotations.derivedComponent
import longevity.model.annotations.polyComponent

@polyComponent
sealed trait ControlledVocab

@derivedComponent[ControlledVocab]
case class VocabTerm1() extends ControlledVocab

@derivedComponent[ControlledVocab]
case object VocabTerm2 extends ControlledVocab
