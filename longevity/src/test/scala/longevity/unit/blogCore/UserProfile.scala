package longevity.unit.blogCore

import longevity.model.annotations.component

@component[BlogCore]
case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)
