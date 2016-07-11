package longevity.integration.subdomain.complexConstraint

import longevity.subdomain.KeyVal

case class ComplexConstraintId(
  id: String)
extends KeyVal[ComplexConstraint](
  ComplexConstraint.keys.id)
