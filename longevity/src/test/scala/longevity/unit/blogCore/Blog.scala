package longevity.unit.blogCore

import longevity.model.annotations.persistent

@persistent[BlogCore]
case class Blog(
  uri: BlogUri,
  title: String,
  description: Markdown,
  authors: Set[Username])

object Blog {
  implicit val uriKey = key(props.uri)
}
