package longevity.integration.subdomain.attributeSets

import longevity.subdomain.KeyVal

case class AttributeSetsId(id: String)
extends KeyVal[AttributeSets](AttributeSets.keys.id)
