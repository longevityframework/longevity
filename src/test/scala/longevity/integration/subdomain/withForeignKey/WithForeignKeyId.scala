package longevity.integration.subdomain.withForeignKey

import longevity.subdomain.KeyVal

case class WithForeignKeyId(
  id: String)
extends KeyVal[WithForeignKey](
  WithForeignKey.keys.id)
