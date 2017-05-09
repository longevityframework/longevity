package longevity.unit

package object blogCore {

  import longevity.model.CType
  import longevity.model.CTypePool
  import longevity.model.KeyVal
  import longevity.model.PType
  import longevity.model.PTypePool
  import longevity.model.ModelType

  case class Email(email: String) extends KeyVal[User]

  case class Username(username: String) extends KeyVal[User]

  case class Markdown(markdown: String)

  case class Uri(uri: String)

  implicit def toEmail(email: String) = Email(email)
  implicit def toUsername(username: String) = Username(username)
  implicit def toMarkdown(markdown: String) = Markdown(markdown)
  implicit def toUri(uri: String) = Uri(uri)

  case class User(
    username: Username,
    email: Email,
    fullname: String,
    profile: Option[UserProfile] = None) 

  object User extends PType[User] {
    object props {
      val username = prop[Username]("username")
      val email = prop[Email]("email")
    }
    lazy val keySet = Set(key(props.username), key(props.email))
  }

  case class UserProfile(
    tagline: String,
    imageUri: Uri,
    description: Markdown) 

  case class BlogUri(uri: Uri) extends KeyVal[Blog]

  case class Blog(
    uri: BlogUri,
    title: String,
    description: Markdown,
    authors: Set[Username]) 

  object Blog extends PType[Blog] {
    object props {
      val uri = prop[BlogUri]("uri")
    }
    lazy val keySet = Set(key(props.uri))
  }

  case class BlogPostUri(uri: Uri) extends KeyVal[BlogPost]

  case class BlogPost(
    uri: BlogPostUri,
    title: String,
    slug: Option[Markdown] = None,
    content: Markdown,
    labels: Set[String] = Set(),
    blog: BlogUri,
    authors: Set[Username]) 

  object BlogPost extends PType[BlogPost] {
    object props {
      val uri = prop[BlogPostUri]("uri")
      val blog = prop[BlogUri]("blog")
    }
    lazy val keySet = Set(key(props.uri))
    override lazy val indexSet = Set(index(props.blog))
  }

  def modelType = ModelType(
    PTypePool(User, Blog, BlogPost),
    CTypePool(CType[Markdown], CType[Uri], CType[UserProfile]))

}
