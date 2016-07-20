package longevity.integration.quickStart

import com.github.nscala_time.time.Implicits.richDateTime
import com.github.nscala_time.time.Implicits.richInt
import org.joda.time.DateTime
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.concurrent.ScaledTimeSpans
import org.scalatest.time.Millis
import org.scalatest.time.Span
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// i would pull this, but unit/manual/RepoSpec.scala depends on it. i
// should rework that class so i can delete this one

/** demonstrates how to get started quickly with longevity. please
 * read the manual when you get the chance  :)
 * 
 * @see http://longevityframework.github.io/longevity/quick-start.html
 * @see http://longevityframework.github.io/longevity/manual
 */
object QuickStartSpec {

  // set up your library dependencies in sbt:

  // resolvers += Resolver.sonatypeRepo("releases")
  // libraryDependencies += "org.longevityframework" %% "longevity" % "0.9.0"

  // bring in library dependencies for either mongo or cassandra:

  // libraryDependencies += "org.longevityframework" %% "longevity-cassandra-deps" % "0.9.0"
  // libraryDependencies += "org.longevityframework" %% "longevity-mongo-deps" % "0.9.0"

  // start building our subdomain:

  import longevity.subdomain.KeyVal
  import longevity.subdomain.Subdomain
  import longevity.subdomain.embeddable.Entity
  import longevity.subdomain.embeddable.EntityType
  import longevity.subdomain.embeddable.ETypePool
  import longevity.subdomain.embeddable.ValueObject
  import longevity.subdomain.embeddable.ValueType
  import longevity.subdomain.persistent.Root
  import longevity.subdomain.ptype.PTypePool
  import longevity.subdomain.ptype.RootType

  // value objects to build your entities with:

  case class Markdown(markdown: String) extends ValueObject
  object Markdown extends ValueType[Markdown]

  case class Uri(uri: String) extends ValueObject
  object Uri extends ValueType[Uri]

  // some convenience methods for using value objects:

  implicit def toMarkdown(markdown: String) = Markdown(markdown)
  implicit def toUri(uri: String) = Uri(uri)

  // now define your three aggregates: user, blog, and blog post:

  // user has two keys:

  import longevity.subdomain.KeyVal

  case class Username(username: String)
  extends KeyVal[User, Username](User.keys.username)

  case class Email(email: String)
  extends KeyVal[User, Email](User.keys.email)

  implicit def toUsername(username: String) = Username(username)
  implicit def toEmail(email: String) = Email(email)

  case class User(
    username: Username,
    fullname: String,
    email: Email,
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
    object indexes {
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
    object indexes {
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
    postDate: DateTime,
    blog: BlogUri,
    authors: Set[Username])
  extends Root

  object BlogPost extends RootType[BlogPost] {
    object props {
      val uri = prop[BlogPostUri]("uri")
      val blog = prop[BlogUri]("blog")
      val postDate = prop[DateTime]("postDate")
    }
    object keys {
      val uri = key(props.uri)
    }
    object indexes {
      val recentPosts = index(props.blog, props.postDate)
    }
  }

  // build the subdomain:

  val blogCore = Subdomain(
    "blogging",
    PTypePool(User, Blog, BlogPost),
    ETypePool(Markdown, Uri, UserProfile))

  // now build the context:

  import longevity.context._

  val context = LongevityContext(blogCore, Mongo)

  // create some persistent objects:

  val john = User("smithy", "John Smith", "smithy@john-smith.ninja")
  val frank = User("franky", "Francis Nickerson", "franky@john-smith.ninja")
  val jerry = User("jerry", "Jerry Jones", "jerry@john-smith.ninja")

  val blog = Blog(
    uri = BlogUri("http://blog.john-smith.ninja/"),
    title = "The Blogging Ninjas",
    description = "We try to keep things interesting blogging about ninjas.",
    authors = Set(john.username, frank.username))

  val johnsPost = BlogPost(
    uri = BlogPostUri("johns_first_post"),
    title = "John's first post",
    content = "_work in progress_",
    postDate = DateTime.now,
    blog = blog.uri,
    authors = Set(john.username))

  val franksPost = BlogPost(
    uri = BlogPostUri("franks_first_post"),
    title = "Frank's first post",
    content = "_work in progress_",
    postDate = DateTime.now,
    blog = blog.uri,
    authors = Set(frank.username))

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
  import longevity.subdomain.persistent.Persistent
  import longevity.persistence._

  // get the repositories:

  // normally we would use `context.repoPool` here, but since this is actually
  // a test, we will use the test DB:
  val repos = context.testRepoPool

  val userRepo = repos[User]
  val blogRepo = repos[Blog]
  val blogPostRepo = repos[BlogPost]

  "QuickStartSpec" should "exercise basic longevity functionality" in {

    // persist the aggregates all at once:
    val createManyResult: Future[Seq[PState[_ <: Persistent]]] =
      repos.createMany(john, frank, blog, johnsPost, franksPost)

    // `futureValue` is a ScalaTest way of saying "wait for the future to
    // complete and assert success"
    createManyResult.futureValue

    // retrieve an entity:

    // `Repo[User].retrieve` returns a `Future[Option[PState[User]]]`,
    // aka `FOPState[User]`
    val retrieveResult: FOPState[User] = userRepo.retrieve(john.username)

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

    val newPost = BlogPost(
      uri = BlogPostUri("new_post"),
      title = "New post",
      content = "_work in progress_",
      postDate = DateTime.now,
      blog = blog.uri,
      authors = Set(john.username))

    val futurePostState: Future[PState[BlogPost]] =
      blogPostRepo.create(newPost)

    // delete the new post:

    val postState = futurePostState.futureValue
    blogPostRepo.delete(postState).futureValue

    // add a new author to a blog:

    userRepo.create(jerry).futureValue

    val blogState: PState[Blog] = blogRepo.retrieveOne(blog.uri).futureValue
    val modifiedBlogState = blogState.map { blog =>
      blog.copy(authors = blog.authors + jerry.username)
    }
    blogRepo.update(modifiedBlogState).futureValue

    // suppose we have two service methods that work on a user:

    object userService {
      def updateUser(user: User): User = user
      def updateUserReactive(user: User): Future[User] = Future.successful(user)
    }

    // we can apply the first service method like so:

    val updated: FPState[User] = for {
      originalState <- userRepo.retrieveOne(john.username)
      modifiedState = originalState.map(userService.updateUser)
      updatedState <- userRepo.update(modifiedState)
    } yield updatedState

    updated.futureValue

    // and we can apply the reactive service method like so:

    val updatedReactive: FPState[User] = for {
      originalState <- userRepo.retrieveOne(john.username)
      modifiedUser <- userService.updateUserReactive(originalState.get)
      modifiedState = originalState.set(modifiedUser)
      updatedState <- userRepo.update(modifiedState)
    } yield updatedState

    updatedReactive.futureValue

    // use a `KeyVal` to retrieve an author from a blog post:

    val authorUsername: Username = johnsPost.authors.head
    val author: FPState[User] = userRepo.retrieveOne(authorUsername)

    // find posts for a given blog published in the last week:

    import BlogPost.queryDsl._
    val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery(
      BlogPost.props.blog eqs blog.uri and
      BlogPost.props.postDate gt DateTime.now - 1.week)
    recentPosts.futureValue.size should equal (2)

    // same thing without the DSL:

    import longevity.subdomain.ptype.Query
    val noDsl: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery(
      Query.and(
        Query.eqs(BlogPost.props.blog, blog.uri),
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
    val futureDeleted = for {
      optUserState <- userRepo retrieve user.username
      deleted <- optUserState map userRepo.delete getOrElse Future.successful(())
    } yield deleted
    futureDeleted.futureValue
  }

  private def deletePost(post: BlogPost): Unit = {
    val futureDeleted = for {
      optPostState <- blogPostRepo.retrieve(post.uri)
      deleted <- optPostState map blogPostRepo.delete getOrElse Future.successful(())
    } yield deleted
    futureDeleted.futureValue
  }

  private def deleteBlog(blog: Blog): Unit = {
    val futureDeleted = for {
      optBlogState <- blogRepo retrieve blog.uri
      deleted <- optBlogState map blogRepo.delete getOrElse Future.successful(())
    } yield deleted
    futureDeleted.futureValue
  }

}
