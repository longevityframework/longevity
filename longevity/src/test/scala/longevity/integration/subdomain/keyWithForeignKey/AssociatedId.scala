package longevity.integration.subdomain.keyWithForeignKey

import longevity.subdomain.KeyVal

case class AssociatedId(id: String)
extends KeyVal[Associated, AssociatedId]
