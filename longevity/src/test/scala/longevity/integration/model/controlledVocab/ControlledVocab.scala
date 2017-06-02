package longevity.integration.model.controlledVocab

import longevity.model.annotations.derivedComponent
import longevity.model.annotations.polyComponent

@polyComponent[DomainModel]
sealed trait ControlledVocab

@derivedComponent[DomainModel, ControlledVocab]
case class VocabTerm1() extends ControlledVocab

@derivedComponent[DomainModel, ControlledVocab]
case object VocabTerm2 extends ControlledVocab
