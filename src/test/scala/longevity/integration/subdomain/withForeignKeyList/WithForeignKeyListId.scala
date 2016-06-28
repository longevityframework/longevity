package longevity.integration.subdomain.withForeignKeyList

import longevity.subdomain.KeyVal

case class WithForeignKeyListId(
  id: String)
extends KeyVal[WithForeignKeyList](
  WithForeignKeyList.keys.id)
