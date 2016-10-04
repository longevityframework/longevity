package longevity.integration.subdomain.multipleKeys

import longevity.subdomain.KeyVal

case class MultipleKeysId(
  id: String)
extends KeyVal[MultipleKeys, MultipleKeysId](
  MultipleKeys.keys.id)
