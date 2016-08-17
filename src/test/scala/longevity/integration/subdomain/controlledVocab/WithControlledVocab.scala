package longevity.integration.subdomain.controlledVocab

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithControlledVocab(
  id: WithControlledVocabId,
  vocab: ControlledVocab)
extends Root 

object WithControlledVocab extends RootType[WithControlledVocab] {
  object props {
    val id = prop[WithControlledVocabId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
