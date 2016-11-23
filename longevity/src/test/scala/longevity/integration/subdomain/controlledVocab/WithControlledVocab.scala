package longevity.integration.subdomain.controlledVocab

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class WithControlledVocab(
  id: WithControlledVocabId,
  vocab: ControlledVocab)
