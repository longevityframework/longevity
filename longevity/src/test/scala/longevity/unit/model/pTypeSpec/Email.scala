package longevity.unit.model.pTypeSpec

import longevity.model.annotations.keyVal

@keyVal[DomainModel, EmailedUser]
case class Email(email: String)
