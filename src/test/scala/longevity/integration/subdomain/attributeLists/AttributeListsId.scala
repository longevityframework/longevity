package longevity.integration.subdomain.attributeLists

import longevity.subdomain.KeyVal

case class AttributeListsId(id: String)
extends KeyVal[AttributeLists](AttributeLists.keys.id)
