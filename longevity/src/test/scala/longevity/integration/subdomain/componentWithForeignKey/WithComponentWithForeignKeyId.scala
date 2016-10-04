package longevity.integration.subdomain.componentWithForeignKey

import longevity.subdomain.KeyVal

case class WithComponentWithForeignKeyId(
  id: String)
extends KeyVal[WithComponentWithForeignKey, WithComponentWithForeignKeyId](
  WithComponentWithForeignKey.keys.id)
