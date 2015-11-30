package longevity.integration.quickStart

import org.scalatest.OptionValues._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time.SpanSugar._
import scala.concurrent.Future

/** demonstrates how to get started quickly with longevity. please
 * read the manual when you get the chance  :)
 * 
 * @see http://sullivan-.github.io/longevity/quick-start.html
 * @see http://sullivan-.github.io/longevity/manual
 */
object QuickStartSpec {

  // set up your library dependencies in sbt:

  // resolvers += Resolver.sonatypeRepo("releases")
  // libraryDependencies += "net.jsmscs" %% "longevity" % "0.3.0-SNAPSHOT"

  // if you are using mongo, get the mongo driver:

  // libraryDependencies += "org.mongodb" %% "casbah" % "3.0.0"

  // for cassandra, you need a driver and some serialization utils as well (for now):

  // libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "2.2.0-rc3"
  // libraryDependencies += "com.twitter" %% "chill" % "0.7.1"
  // libraryDependencies += "com.twitter" %% "chill-bijection" % "0.7.1"
  // libraryDependencies += "de.javakaffee" % "kryo-serializers" % "0.37"

  // start building our subdomain:

  import longevity.subdomain._

  // define your shorthand classes:

  case class Email(email: String)
  case class Markdown(markdown: String)
  case class Uri(uri: String)

  // some convenience methods for using shorthands:

  implicit def toEmail(email: String) = Email(email)
  implicit def toMarkdown(markdown: String) = Markdown(markdown)
  implicit def toUri(uri: String) = Uri(uri)

  // build your shorthand pool:

  implicit val shorthandPool = ShorthandPool(
    Shorthand[Email, String],
    Shorthand[Markdown, String],
    Shorthand[Uri, String])

  // now define your three aggregates: user, blog, and blog post:

  case class User(
    username: String,
    fullname: String,
    email: Email,
    profile: Option[UserProfile] = None)
  extends RootEntity

  object User extends RootEntityType[User] {
    val usernameKey = key("username")
    val emailKey = key("email")
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
  extends RootEntity

  object Blog extends RootEntityType[Blog] {
    val natKey = key("uri")
  }

  case class BlogPost(
    uriPathSuffix: String,
    title: String,
    slug: Option[Markdown] = None,
    content: Markdown,
    labels: Set[String] = Set(),
    blog: Assoc[Blog],
    authors: Set[Assoc[User]])
  extends RootEntity

  object BlogPost extends RootEntityType[BlogPost] {
    val natKey = key("blog", "uriPathSuffix")
  }

  // build the subdomain:

  object BlogCore extends Subdomain("blogging", EntityTypePool(User, UserProfile, Blog, BlogPost))

  // you can also build your subdomain like this, but check here quickly for caveat:
  // http://sullivan-.github.io/longevity/manual/subdomain/where-not.html
  //
  // val blogCore = Subdomain("blogging", EntityTypePool(User, Blog, BlogPost))

  // now build the context:

  import longevity.context._

  val context = LongevityContext(BlogCore, Mongo)

}

class QuickStartSpec extends FlatSpec with GivenWhenThen with Matchers with ScalaFutures with ScaledTimeSpans {

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(4000 millis),
    interval = scaled(50 millis))

  import QuickStartSpec._
  import longevity.subdomain._
  import longevity.persistence._

  "QuickStartSpec" should "exercise basic longevity functionality" in {

    // create some entities:

    val john = User("smithy", "John Smith", "smithy@john-smith.ninja")
    val frank = User("franky", "Francis Nickerson", "franky@john-smith.ninja")

    val blog = Blog(
      uri = "http://blog.john-smith.ninja/",
      title = "The Blogging Ninjas",
      description = "We try to keep things interesting blogging about ninjas.",
      authors = Set(Assoc(john), Assoc(frank)))

    val johnsPost = BlogPost(
      uriPathSuffix = "johns_first_post",
      title = "John's first post",
      content = "_work in progress_",
      blog = blog,
      authors = Set(Assoc(john)))

    val franksPost = BlogPost(
      uriPathSuffix = "franks_first_post",
      title = "Frank's first post",
      content = "_work in progress_",
      blog = blog,
      authors = Set(Assoc(frank)))

    // get the repo pool:

    val repos = context.repoPool

    // persist the entities:

    repos[User].create(john).futureValue
    repos[User].create(frank).futureValue
    repos[Blog].create(blog).futureValue
    repos[BlogPost].create(johnsPost).futureValue
    repos[BlogPost].create(franksPost).futureValue

    // you can create these entities in any order. you also don't need
    // to explicitly create the blog, as it will be handled
    // recursively when creating one of the blog posts

    // retrieve an entity:

    val retrieveResult: Future[Option[Persisted[User]]] =
      repos[User].retrieve(
        User.usernameKey)(
        User.usernameKey.builder.setProp("username", john.username).build)

    // in time, we'll develop a DSL for creating key values more easily

    // unwrap the future and option:

    val userState: Persisted[User] = retrieveResult.futureValue.value

    // unwrap the persistent state:

    val user: User = userState.get
    user should equal (john)

    // modify the user and persist the change:

    val modified: Persisted[User] =
      userState.map { user: User => user.copy(fullname = "John Smith Jr.") }

    val updateResult: Future[Persisted[User]] = repos[User].update(modified)
    val updatedUserState: Persisted[User] = updateResult.futureValue

    // add a new author to a blog:

    val newUserState = repos[User].create(
      User("jerry", "Jerry Jones", "jerry@john-smith.ninja")).futureValue
    val blogKeyVal: Blog.natKey.Val = Blog.natKey.builder.setProp("uri", blog.uri).build
    val blogState: Persisted[Blog] =
      repos[Blog].retrieve(Blog.natKey)(blogKeyVal).futureValue.value
    val modifiedBlogState = blogState.map { blog =>
      blog.copy(authors = blog.authors + updatedUserState.assoc)
    }
    repos[Blog].update(modifiedBlogState)

    // TODO example using an assoc
  }

}
