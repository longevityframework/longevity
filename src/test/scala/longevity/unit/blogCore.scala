package longevity.unit

package object blogCore {

  import longevity.subdomain._

  case class Email(email: String)
  case class Markdown(markdown: String)
  case class Uri(uri: String)

  implicit def toEmail(email: String) = Email(email)
  implicit def toMarkdown(markdown: String) = Markdown(markdown)
  implicit def toUri(uri: String) = Uri(uri)

  implicit val shorthandPool = ShorthandPool(
    Shorthand[Email, String],
    Shorthand[Markdown, String],
    Shorthand[Uri, String])

  case class User(
    username: String,
    fullname: String,
    email: Email,
    profile: Option[UserProfile] = None)
       extends Root

  object User extends RootType[User] {
    val usernameProp = prop[String]("username")
    val emailProp = prop[Email]("email")
    val usernameKey = key(usernameProp)
    val emailKey = key(emailProp)
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
    val uriProp = prop[Uri]("uri")
    val natKey = key(uriProp)
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
    val blogProp = prop[Assoc[Blog]]("blog")
    val suffixProp = prop[String]("uriPathSuffix")
    val natKey = key(blogProp, suffixProp)
  }

  object BlogCore extends Subdomain("blogging", EntityTypePool(User, UserProfile, Blog, BlogPost))

}
