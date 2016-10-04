package longevity.unit

package object blogCore {

  import longevity.subdomain.Embeddable
  import longevity.subdomain.EType
  import longevity.subdomain.ETypePool
  import longevity.subdomain.Embeddable
  import longevity.subdomain.EType
  import longevity.subdomain.KeyVal
  import longevity.subdomain.Subdomain
  import longevity.ddd.subdomain.Root
  import longevity.subdomain.PTypePool
  import longevity.subdomain.PType

  case class Email(email: String)
  extends KeyVal[User, Email](User.keys.email)

  case class Username(username: String)
  extends KeyVal[User, Username](User.keys.username)

  case class Markdown(markdown: String) extends Embeddable

  case class Uri(uri: String) extends Embeddable

  implicit def toEmail(email: String) = Email(email)
  implicit def toUsername(username: String) = Username(username)
  implicit def toMarkdown(markdown: String) = Markdown(markdown)
  implicit def toUri(uri: String) = Uri(uri)

  case class User(
    username: Username,
    email: Email,
    fullname: String,
    profile: Option[UserProfile] = None)
  extends Root

  object User extends PType[User] {
    object props {
      val username = prop[Username]("username")
      val email = prop[Email]("email")
    }
    object keys {
      val username = key(props.username)
      val email = key(props.email)
    }
  }

  case class UserProfile(
    tagline: String,
    imageUri: Uri,
    description: Markdown)
  extends Embeddable

  case class BlogUri(uri: Uri)
  extends KeyVal[Blog, BlogUri](Blog.keys.uri)

  case class Blog(
    uri: BlogUri,
    title: String,
    description: Markdown,
    authors: Set[Username])
  extends Root

  object Blog extends PType[Blog] {
    object props {
      val uri = prop[BlogUri]("uri")
    }
    object keys {
      val uri = key(props.uri)
    }
  }

  case class BlogPostUri(uri: Uri)
  extends KeyVal[BlogPost, BlogPostUri](BlogPost.keys.uri)

  case class BlogPost(
    uri: BlogPostUri,
    title: String,
    slug: Option[Markdown] = None,
    content: Markdown,
    labels: Set[String] = Set(),
    blog: BlogUri,
    authors: Set[Username])
  extends Root

  object BlogPost extends PType[BlogPost] {
    object props {
      val uri = prop[BlogPostUri]("uri")
      val blog = prop[BlogUri]("blog")
    }
    object keys {
      val uri = key(props.uri)
    }
    object indexes {
      val blog = props.blog
    }
  }

  object BlogCore extends Subdomain(
    "blogging",
    PTypePool(User, Blog, BlogPost),
    ETypePool(EType[Markdown], EType[Uri], EType[UserProfile]))

}
