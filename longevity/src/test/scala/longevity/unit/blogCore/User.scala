package longevity.unit.blogCore

import longevity.model.annotations.persistent

@persistent[BlogCore]
case class User(
  username: Username,
  email: Email,
  fullname: String,
  profile: Option[UserProfile] = None)

object User {
  implicit val usernameKey = key(props.username)
  implicit val emailKey = key(props.email)
}
