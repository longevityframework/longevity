package longevity.integration.subdomain.foreignKeySet

import longevity.subdomain.PType

case class WithForeignKeySet(
  id: WithForeignKeySetId,
  associated: Set[AssociatedId])

object WithForeignKeySet extends PType[WithForeignKeySet] {
  object props {
    val id = prop[WithForeignKeySetId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
