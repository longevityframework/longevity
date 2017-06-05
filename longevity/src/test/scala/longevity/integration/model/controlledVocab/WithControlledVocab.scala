package longevity.integration.model.controlledVocab

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithControlledVocab(
  id: WithControlledVocabId,
  vocab: ControlledVocab)

object WithControlledVocab {
  implicit lazy val idKey = key(props.id)
}
