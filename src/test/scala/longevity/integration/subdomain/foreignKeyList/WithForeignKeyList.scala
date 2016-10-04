package longevity.integration.subdomain.foreignKeyList

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.PType

case class WithForeignKeyList(
  id: WithForeignKeyListId,
  associated: List[AssociatedId])
extends Root

object WithForeignKeyList extends PType[WithForeignKeyList] { 
  object props {
    val id = prop[WithForeignKeyListId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
