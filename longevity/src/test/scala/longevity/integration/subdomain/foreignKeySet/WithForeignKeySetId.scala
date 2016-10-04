package longevity.integration.subdomain.foreignKeySet

import longevity.subdomain.KeyVal

case class WithForeignKeySetId(
  id: String)
extends KeyVal[WithForeignKeySet, WithForeignKeySetId](
  WithForeignKeySet.keys.id)
