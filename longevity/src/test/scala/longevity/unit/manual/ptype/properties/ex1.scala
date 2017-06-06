package longevity.unit.manual.ptype.properties.ex1

import longevity.model.annotations.component
import longevity.model.annotations.persistent

@component[DomainModel] case class Email(email: String)
@component[DomainModel] case class Markdown(markdown: String)
@component[DomainModel] case class Uri(uri: String)

@component[DomainModel]
case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

@persistent[DomainModel]
case class User(
  username: String,
  email: Email,
  profile: UserProfile)
