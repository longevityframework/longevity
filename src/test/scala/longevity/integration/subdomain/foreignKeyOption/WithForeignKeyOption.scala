package longevity.integration.subdomain.foreignKeyOption

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

case class WithForeignKeyOption(
  id: WithForeignKeyOptionId,
  associated: Option[AssociatedId])
extends Root

object WithForeignKeyOption extends RootType[WithForeignKeyOption] {
  object props {
    val id = prop[WithForeignKeyOptionId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
