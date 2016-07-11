package longevity.integration.subdomain.componentList

import longevity.subdomain.KeyVal

case class WithComponentListId(
  id: String)
extends KeyVal[WithComponentList](
  WithComponentList.keys.id)
