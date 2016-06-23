package longevity.unit

package object blogCore {

  import longevity.subdomain.Assoc
  import longevity.subdomain.embeddable.Entity
  import longevity.subdomain.embeddable.EntityType
  import longevity.subdomain.embeddable.ETypePool
  import longevity.subdomain.embeddable.ValueObject
  import longevity.subdomain.embeddable.ValueType
  import longevity.subdomain.Subdomain
  import longevity.subdomain.persistent.Root
  import longevity.subdomain.ptype.PTypePool
  import longevity.subdomain.ptype.RootType

  case class Email(email: String) extends ValueObject
  object Email extends ValueType[Email]

  case class Markdown(markdown: String) extends ValueObject
  object Markdown extends ValueType[Markdown]

  case class Uri(uri: String) extends ValueObject
  object Uri extends ValueType[Uri]

  implicit def toEmail(email: String) = Email(email)
  implicit def toMarkdown(markdown: String) = Markdown(markdown)
  implicit def toUri(uri: String) = Uri(uri)

  case class User(
    username: String,
    fullname: String,
    email: Email,
    profile: Option[UserProfile] = None)
  extends Root

  object User extends RootType[User] {
    object props {
      val username = prop[String]("username")
      val email = prop[Email]("email")
    }
    object keys {
      val username = key(props.username)
      val email = key(props.email)
    }
    object indexes {
    }
  }

  case class UserProfile(
    tagline: String,
    imageUri: Uri,
    description: Markdown)
  extends Entity

  object UserProfile extends EntityType[UserProfile]

  case class Blog(
    uri: Uri,
    title: String,
    description: Markdown,
    authors: Set[Assoc[User]])
  extends Root

  object Blog extends RootType[Blog] {
    object props {
      val uri = prop[Uri]("uri")
    }
    object keys {
      val uri = key(props.uri)
    }
    object indexes {
    }
  }

  case class BlogPost(
    uriPathSuffix: String,
    title: String,
    slug: Option[Markdown] = None,
    content: Markdown,
    labels: Set[String] = Set(),
    blog: Assoc[Blog],
    authors: Set[Assoc[User]])
  extends Root

  object BlogPost extends RootType[BlogPost] {
    object props {
      val blog = prop[Assoc[Blog]]("blog")
      val suffix = prop[String]("uriPathSuffix")
    }
    object keys {
      val uri = key(props.blog, props.suffix)
    }
    object indexes {
    }
  }

  object BlogCore extends Subdomain(
    "blogging",
    PTypePool(User, Blog, BlogPost),
    ETypePool(Email, Markdown, Uri, UserProfile))

}
