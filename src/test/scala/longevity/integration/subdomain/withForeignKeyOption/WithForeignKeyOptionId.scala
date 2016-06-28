package longevity.integration.subdomain.withForeignKeyOption

import longevity.subdomain.KeyVal

case class WithForeignKeyOptionId(
  id: String)
extends KeyVal[WithForeignKeyOption](
  WithForeignKeyOption.keys.id)
