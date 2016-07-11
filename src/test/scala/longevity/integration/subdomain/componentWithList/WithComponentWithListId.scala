package longevity.integration.subdomain.componentWithList

import longevity.subdomain.KeyVal

case class WithComponentWithListId(id: String)
extends KeyVal[WithComponentWithList](WithComponentWithList.keys.id)
