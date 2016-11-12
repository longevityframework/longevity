package longevity.integration.subdomain.foreignKeyOption

import longevity.subdomain.PType

case class WithForeignKeyOption(
  id: WithForeignKeyOptionId,
  associated: Option[AssociatedId])

object WithForeignKeyOption extends PType[WithForeignKeyOption] {
  object props {
    val id = prop[WithForeignKeyOptionId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
