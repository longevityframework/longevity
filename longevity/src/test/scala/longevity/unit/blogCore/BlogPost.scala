package longevity.unit.blogCore

import longevity.model.annotations.persistent

@persistent[BlogCore]
case class BlogPost(
  uri: BlogPostUri,
  title: String,
  slug: Option[Markdown] = None,
  content: Markdown,
  labels: Set[String] = Set(),
  blog: BlogUri,
  authors: Set[Username])

object BlogPost {
  implicit val uriKey = key(props.uri)
  override val indexSet = Set(index(props.blog))
}
