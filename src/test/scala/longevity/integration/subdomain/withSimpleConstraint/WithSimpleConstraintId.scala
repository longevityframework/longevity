package longevity.integration.subdomain.withSimpleConstraint

import longevity.subdomain.KeyVal

case class WithSimpleConstraintId(
  id: String)
extends KeyVal[WithSimpleConstraint](
  WithSimpleConstraint.keys.id)


