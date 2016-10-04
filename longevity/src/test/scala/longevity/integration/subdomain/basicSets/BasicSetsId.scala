package longevity.integration.subdomain.basicSets

import longevity.subdomain.KeyVal

case class BasicSetsId(id: String)
extends KeyVal[BasicSets, BasicSetsId](BasicSets.keys.id)
