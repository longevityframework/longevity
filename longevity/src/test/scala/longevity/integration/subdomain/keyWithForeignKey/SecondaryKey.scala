package longevity.integration.subdomain.keyWithForeignKey

import longevity.subdomain.KeyVal

case class SecondaryKey(
  id: String,
  associated: AssociatedId)
extends KeyVal[KeyWithForeignKey]
