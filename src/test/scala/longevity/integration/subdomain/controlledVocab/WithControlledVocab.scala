package longevity.integration.subdomain.controlledVocab

import longevity.ddd.subdomain.Root
import longevity.subdomain.PType

case class WithControlledVocab(
  id: WithControlledVocabId,
  vocab: ControlledVocab)
extends Root 

object WithControlledVocab extends PType[WithControlledVocab] {
  object props {
    val id = prop[WithControlledVocabId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
