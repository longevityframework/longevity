package longevity.integration.subdomain.foreignKeyList

import longevity.subdomain.KeyVal

case class WithForeignKeyListId(
  id: String)
extends KeyVal[WithForeignKeyList, WithForeignKeyListId](
  WithForeignKeyList.keys.id)
