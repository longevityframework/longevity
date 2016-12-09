package longevity.unit.model.annotations.subdomainExample

import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent
import longevity.model.annotations.{ domainModel => dm }

@dm object subdomain

@keyVal[User] case class Username(username: String)

@persistent(keySet = Set(key(props.username)))
case class User(
  username: Username,
  firstName: String,
  lastName: String)