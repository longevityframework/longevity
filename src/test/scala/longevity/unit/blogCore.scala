package longevity.unit

package object blogCore {

  import longevity.subdomain.embeddable.Entity
  import longevity.subdomain.embeddable.EntityType
  import longevity.subdomain.embeddable.ETypePool
  import longevity.subdomain.embeddable.ValueObject
  import longevity.subdomain.embeddable.ValueType
  import longevity.subdomain.KeyVal
  import longevity.subdomain.Subdomain
  import longevity.subdomain.persistent.Root
  import longevity.subdomain.ptype.PTypePool
  import longevity.subdomain.ptype.RootType

  case class Email(email: String)
  extends KeyVal[User, Email](User.keys.email)

  case class Username(username: String)
  extends KeyVal[User, Username](User.keys.username)

  case class Markdown(markdown: String) extends ValueObject
  object Markdown extends ValueType[Markdown]

  case class Uri(uri: String) extends ValueObject
  object Uri extends ValueType[Uri]

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

  object User extends RootType[User] {
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
  extends Entity

  object UserProfile extends EntityType[UserProfile]

  case class BlogUri(uri: Uri)
  extends KeyVal[Blog, BlogUri](Blog.keys.uri)

  case class Blog(
    uri: BlogUri,
    title: String,
    description: Markdown,
    authors: Set[Username])
  extends Root

  object Blog extends RootType[Blog] {
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

  object BlogPost extends RootType[BlogPost] {
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
    ETypePool(Markdown, Uri, UserProfile))

}
