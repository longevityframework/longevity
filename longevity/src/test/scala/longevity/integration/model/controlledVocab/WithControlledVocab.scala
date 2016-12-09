package longevity.integration.model.controlledVocab

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class WithControlledVocab(
  id: WithControlledVocabId,
  vocab: ControlledVocab)
