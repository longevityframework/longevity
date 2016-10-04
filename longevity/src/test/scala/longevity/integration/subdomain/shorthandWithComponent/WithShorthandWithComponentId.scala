package longevity.integration.subdomain.shorthandWithComponent

import longevity.subdomain.KeyVal

case class WithShorthandWithComponentId(
  id: String)
extends KeyVal[WithShorthandWithComponent, WithShorthandWithComponentId](
  WithShorthandWithComponent.keys.id)
