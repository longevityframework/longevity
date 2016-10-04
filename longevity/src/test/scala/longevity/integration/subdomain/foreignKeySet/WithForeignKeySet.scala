package longevity.integration.subdomain.foreignKeySet

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithForeignKeySet(
  id: WithForeignKeySetId,
  associated: Set[AssociatedId])
extends Persistent

object WithForeignKeySet extends PType[WithForeignKeySet] {
  object props {
    val id = prop[WithForeignKeySetId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
