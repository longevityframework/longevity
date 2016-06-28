package longevity.integration.subdomain.oneAttribute

import longevity.subdomain.KeyVal

case class OneAttributeId(id: String)
extends KeyVal[OneAttribute](OneAttribute.keys.id)
