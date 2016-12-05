package longevity.unit.subdomain.annotations.subdomainExample

import longevity.subdomain.annotations.keyVal
import longevity.subdomain.annotations.persistent
import longevity.subdomain.annotations.{ subdomain => sub }

@sub object subdomain

@keyVal[User] case class Username(username: String)

@persistent(keySet = Set(key(props.username)))
case class User(
  username: Username,
  firstName: String,
  lastName: String)
