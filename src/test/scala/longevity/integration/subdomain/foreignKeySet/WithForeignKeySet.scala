package longevity.integration.subdomain.foreignKeySet

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithForeignKeySet(
  id: WithForeignKeySetId,
  associated: Set[AssociatedId])
extends Root

object WithForeignKeySet extends RootType[WithForeignKeySet] {
  object props {
    val id = prop[WithForeignKeySetId]("id")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
