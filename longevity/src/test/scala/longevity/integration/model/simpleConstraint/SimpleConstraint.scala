package longevity.integration.model.simpleConstraint

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class SimpleConstraint(
  id: SimpleConstraintId,
  primaryEmail: Email,
  emails: Set[Email])

object SimpleConstraint {
  implicit val idKey = key(props.id)
}
