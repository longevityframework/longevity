package longevity.integration.subdomain.withComponentList

import longevity.subdomain.KeyVal

case class WithComponentListId(
  id: String)
extends KeyVal[WithComponentList](
  WithComponentList.keys.id)
