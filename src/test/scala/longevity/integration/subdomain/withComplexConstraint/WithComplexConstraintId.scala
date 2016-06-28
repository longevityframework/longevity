package longevity.integration.subdomain.withComplexConstraint

import longevity.subdomain.KeyVal

case class WithComplexConstraintId(
  id: String)
extends KeyVal[WithComplexConstraint](
  WithComplexConstraint.keys.id)
