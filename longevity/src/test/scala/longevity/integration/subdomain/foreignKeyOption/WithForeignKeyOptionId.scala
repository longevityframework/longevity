package longevity.integration.subdomain.foreignKeyOption

import longevity.subdomain.KeyVal

case class WithForeignKeyOptionId(
  id: String)
extends KeyVal[WithForeignKeyOption, WithForeignKeyOptionId](
  WithForeignKeyOption.keys.id)
