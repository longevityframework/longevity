package longevity.integration.subdomain.withForeignKeyList

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithForeignKeyList(
  id: WithForeignKeyListId,
  associated: List[AssociatedId])
extends Root

object WithForeignKeyList extends RootType[WithForeignKeyList] { 
  object props {
    val id = prop[WithForeignKeyListId]("id")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
