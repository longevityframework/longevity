package longevity.integration.subdomain.attributeOptions

import longevity.subdomain.KeyVal

case class AttributeOptionsId(id: String)
extends KeyVal[AttributeOptions](AttributeOptions.keys.id)
