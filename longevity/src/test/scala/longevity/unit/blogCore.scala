package longevity.unit

package object blogCore {

  import longevity.model.CType
  import longevity.model.KVType
  import longevity.model.ModelEv
  import longevity.model.ModelType
  import longevity.model.PType

  trait BlogCore

  object BlogCore {
    implicit object modelType extends ModelType[BlogCore](
      Seq(User, Blog, BlogPost),
      Seq(Markdown, Uri, UserProfile))
    implicit object modeEv extends ModelEv[BlogCore]
  }

  case class Email(email: String)

  object Email extends KVType[BlogCore, User, Email]

  case class Username(username: String)

  object Username extends KVType[BlogCore, User, Username]

  case class Markdown(markdown: String)

  object Markdown extends CType[BlogCore, Markdown]

  case class Uri(uri: String)

  object Uri extends CType[BlogCore, Uri]

  implicit def toEmail(email: String) = Email(email)
  implicit def toUsername(username: String) = Username(username)
  implicit def toMarkdown(markdown: String) = Markdown(markdown)
  implicit def toUri(uri: String) = Uri(uri)

  case class User(
    username: Username,
    email: Email,
    fullname: String,
    profile: Option[UserProfile] = None) 

  object User extends PType[BlogCore, User] {
    object props {
      val username = prop[Username]("username")
      val email = prop[Email]("email")
    }
    implicit val usernameKey = key(props.username)
    implicit val emailKey = key(props.email)
  }

  case class UserProfile(
    tagline: String,
    imageUri: Uri,
    description: Markdown) 

  object UserProfile extends CType[BlogCore, UserProfile]

  case class BlogUri(uri: Uri)

  object BlogUri extends KVType[BlogCore, Blog, BlogUri]

  case class Blog(
    uri: BlogUri,
    title: String,
    description: Markdown,
    authors: Set[Username]) 

  object Blog extends PType[BlogCore, Blog] {
    object props {
      val uri = prop[BlogUri]("uri")
    }
    implicit val uriKey = key(props.uri)
  }

  case class BlogPostUri(uri: Uri)

  object BlogPostUri extends KVType[BlogCore, BlogPost, BlogPostUri]

  case class BlogPost(
    uri: BlogPostUri,
    title: String,
    slug: Option[Markdown] = None,
    content: Markdown,
    labels: Set[String] = Set(),
    blog: BlogUri,
    authors: Set[Username]) 

  object BlogPost extends PType[BlogCore, BlogPost] {
    object props {
      val uri = prop[BlogPostUri]("uri")
      val blog = prop[BlogUri]("blog")
    }
    implicit val uriKey = key(props.uri)
    override val indexSet = Set(index(props.blog))
  }

}
