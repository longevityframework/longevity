package longevity.integration.subdomain.componentList

import longevity.subdomain.KeyVal

case class WithComponentListId(
  id: String)
extends KeyVal[WithComponentList, WithComponentListId](
  WithComponentList.keys.id)
