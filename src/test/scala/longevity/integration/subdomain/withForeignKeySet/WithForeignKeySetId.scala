package longevity.integration.subdomain.withForeignKeySet

import longevity.subdomain.KeyVal

case class WithForeignKeySetId(
  id: String)
extends KeyVal[WithForeignKeySet](
  WithForeignKeySet.keys.id)
