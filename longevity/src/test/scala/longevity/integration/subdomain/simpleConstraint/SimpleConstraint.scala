package longevity.integration.subdomain.simpleConstraint

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class SimpleConstraint(
  id: SimpleConstraintId,
  primaryEmail: Email,
  emails: Set[Email])
