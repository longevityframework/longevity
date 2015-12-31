package longevity.integration.quickStart

import com.github.nscala_time.time.Imports._
import org.scalatest.OptionValues._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** demonstrates how to get started quickly with longevity. please
 * read the manual when you get the chance  :)
 * 
 * @see http://longevityframework.github.io/longevity/quick-start.html
 * @see http://longevityframework.github.io/longevity/manual
 */
object QuickStartSpec {

  // set up your library dependencies in sbt:

  // resolvers += Resolver.sonatypeRepo("releases")
  // libraryDependencies += "org.longevityframework" %% "longevity" % "0.4-SNAPSHOT"

  // if you are using mongo, get the mongo driver:

  // libraryDependencies += "org.mongodb" %% "casbah" % "3.0.0"

  // for cassandra, you need a driver and json4s:

  // libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "2.2.0-rc3"
  // TODO pt #107891556 add json4s lib deps

  // start building our subdomain:

  import longevity.subdomain._

  // shorthands help you use typed wrapper classes instead of raw values:

  object shorthands {

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

  }

  import shorthands._

  // now define your three aggregates: user, blog, and blog post:

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
    val usernameKey = key(props.username)
    val emailKey = key(props.email)
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
    val uriKey = key(props.uri)
  }

  case class BlogPost(
    uriPathSuffix: String,
    title: String,
    slug: Option[Markdown] = None,
    content: Markdown,
    labels: Set[String] = Set(),
    postDate: DateTime,
    blog: Assoc[Blog],
    authors: Set[Assoc[User]])
  extends Root

  object BlogPost extends RootType[BlogPost] {
    object props {
      val blog = prop[Assoc[Blog]]("blog")
      val uriPathSuffix = prop[String]("uriPathSuffix")
      val postDate = prop[DateTime]("postDate")
    }
    val uriKey = key(props.blog, props.uriPathSuffix)
  }

  // build the subdomain:

  val blogCore = Subdomain("blogging", EntityTypePool(User, Blog, BlogPost))

  // now build the context:

  import longevity.context._

  val context = LongevityContext(blogCore, Mongo)

  // create some unpersisted entities:

  val john = User("smithy", "John Smith", "smithy@john-smith.ninja")
  val frank = User("franky", "Francis Nickerson", "franky@john-smith.ninja")
  val jerry = User("jerry", "Jerry Jones", "jerry@john-smith.ninja")

  val blog = Blog(
    uri = "http://blog.john-smith.ninja/",
    title = "The Blogging Ninjas",
    description = "We try to keep things interesting blogging about ninjas.",
    authors = Set(Assoc(john), Assoc(frank)))

  val johnsPost = BlogPost(
    uriPathSuffix = "johns_first_post",
    title = "John's first post",
    content = "_work in progress_",
    postDate = DateTime.now,
    blog = Assoc(blog),
    authors = Set(Assoc(john)))

  val franksPost = BlogPost(
    uriPathSuffix = "franks_first_post",
    title = "Frank's first post",
    content = "_work in progress_",
    postDate = DateTime.now,
    blog = Assoc(blog),
    authors = Set(Assoc(frank)))

}

class QuickStartSpec
extends FlatSpec
with BeforeAndAfterAll
with GivenWhenThen
with Matchers
with ScalaFutures
with ScaledTimeSpans {

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(Span(4000, Millis)),
    interval = scaled(Span(50, Millis)))

  import QuickStartSpec._
  import longevity.subdomain._
  import longevity.persistence._

  // get the repo pool:

  // normally we would use `context.repoPool` here, but since this is actually
  // a test, we will use the test DB:
  val repos = context.testRepoPool

  val userRepo = repos[User]
  val blogRepo = repos[Blog]
  val blogPostRepo = repos[BlogPost]

  "QuickStartSpec" should "exercise basic longevity functionality" in {

    // persist the aggregates all at once. it doesn't matter what
    // order you pass them in, this method will assure that associated
    // aggregates always get persisted first.
    val createManyResult: Future[Seq[PState[_ <: Root]]] =
      repos.createMany(john, frank, blog, johnsPost, franksPost)

    // `futureValue` is a ScalaTest way of saying "wait for the future to
    // complete and assert success"
    createManyResult.futureValue

    // retrieve an entity:

    // `Repo[User].retrieve` returns a `Future[Option[PState[User]]]`,
    // aka `FOPState[User]`
    val retrieveResult: FOPState[User] =
      userRepo.retrieve(User.usernameKey(john.username))

    // unwrap the future and option:

    // `value` is a ScalaTest way of saying "assert the Option is defined, and
    // get the contents"
    val userState: PState[User] = retrieveResult.futureValue.value

    // unwrap the persistent state:

    val user: User = userState.get
    user should equal (john)

    // modify the user and persist the change:

    val modified: PState[User] =
      userState.map { user: User => user.copy(fullname = "John Smith Jr.") }

    val updateResult: FPState[User] = userRepo.update(modified)
    val updatedUserState: PState[User] = updateResult.futureValue

    // create a new blog post:

    val blogKeyVal: root.KeyVal[Blog] = Blog.uriKey(blog.uri)
    val blogState: PState[Blog] =
      blogRepo.retrieve(blogKeyVal).futureValue.value

    val newPost = BlogPost(
      uriPathSuffix = "new_post",
      title = "New post",
      content = "_work in progress_",
      postDate = DateTime.now,
      blog = blogState.assoc,
      authors = Set(userState.assoc))

    val futurePostState: Future[PState[BlogPost]] =
      blogPostRepo.create(newPost)

    // clean up the new post:

    val postState = futurePostState.futureValue
    blogPostRepo.delete(postState).futureValue

    // add a new author to a blog:

    val newUserState = userRepo.create(jerry).futureValue
    val modifiedBlogState = blogState.map { blog =>
      blog.copy(authors = blog.authors + updatedUserState.assoc)
    }
    blogRepo.update(modifiedBlogState)

    // there are convenience methods in `FPState` and `FOPState` that allow
    // you to conveniently manipulate the enclosed root. for example, suppose
    // we have two service methods that work on a user:

    object userService {
      def updateUser(user: User): User = user
      def updateUserReactive(user: User): Future[User] = Future.successful(user)
    }

    // we can apply these service methods directly to an `FPState[User]` or an
    // `FOPState[User]`, like so:

    val updated: FOPState[User] =
      userRepo.retrieve(
        User.usernameKey(john.username)
      ).mapRoot(
        userService.updateUser _
      ).flatMapState(
        userRepo.update(_)
      )
    updated.futureValue

    val updatedReactive: FOPState[User] =
      userRepo.retrieve(
        User.usernameKey(john.username)
      ).flatMapRoot(
        userService.updateUserReactive _
      ).flatMapState(
        userRepo.update(_)
      )
    updatedReactive.futureValue

    // use an `Assoc` to retrieve an author from a blog post:

    val post: BlogPost = blogPostRepo.retrieve(
      BlogPost.uriKey(blogState.assoc, johnsPost.uriPathSuffix)
    ).futureValue.value.get
    val authorAssoc: Assoc[User] = post.authors.head
    val author: FPState[User] = authorAssoc.retrieve

    // find posts for a given blog published in the last week:

    import blogPostRepo.queryDsl._
    val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery(
      BlogPost.props.blog eqs blogState.assoc and
      BlogPost.props.postDate gt DateTime.now - 1.week)
    recentPosts.futureValue.size should equal (2)

    // same thing without the DSL:

    import longevity.subdomain.root.Query
    val noDsl: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery(
      Query.and(
        Query.eqs(BlogPost.props.blog, blogState.assoc),
        Query.gt(BlogPost.props.postDate, DateTime.now - 1.week)))
    noDsl.futureValue.size should equal (2)

  }

  // clean up the database after the test:

  override def afterAll = {
    deleteUser(john)
    deleteUser(frank)
    deleteUser(jerry)
    deletePost(johnsPost)
    deletePost(franksPost)
    deleteBlog(blog)
  }

  private def deleteUser(user: User): Unit = {
    def deleteOptUserState(optUserState: Option[PState[User]]): Unit =
      optUserState.foreach { userState => userRepo.delete(userState).futureValue }
    deleteOptUserState(userRepo.retrieve(User.usernameKey.keyValForRoot(user)).futureValue)
  }

  private def deletePost(post: BlogPost): Unit = {
    val deleted = for {
      blog <- blogRepo.retrieve(Blog.uriKey.keyValForRoot(blog)).map(_.get)
      keyValForRoot = BlogPost.uriKey.keyValForRoot(post.copy(blog = blog.assoc))
      post <- blogPostRepo.retrieve(keyValForRoot).map(_.get)
      deleted <- blogPostRepo.delete(post)
    } yield deleted
    deleted.futureValue
  }

  private def deleteBlog(blog: Blog): Unit = {
    blogRepo.retrieve(Blog.uriKey.keyValForRoot(blog)).map(_.get).flatMap(blogRepo.delete _).futureValue
  }

}
