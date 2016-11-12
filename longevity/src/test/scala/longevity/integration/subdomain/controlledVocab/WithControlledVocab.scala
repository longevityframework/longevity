package longevity.integration.subdomain.controlledVocab

import longevity.subdomain.PType

case class WithControlledVocab(
  id: WithControlledVocabId,
  vocab: ControlledVocab)

object WithControlledVocab extends PType[WithControlledVocab] {
  object props {
    val id = prop[WithControlledVocabId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
