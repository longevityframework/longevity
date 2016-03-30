---
title: quick start guide
layout: page
---

Here's a first pass at a quick start guide for using longevity. We're
planning on replacing this with more of a "getting started" guide, but
this is what we have for now:

```scala
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
  // libraryDependencies += "org.longevityframework" %% "longevity" % "0.6.0"

  // bring in library dependencies for either mongo or cassandra:

  // libraryDependencies += "org.longevityframework" %% "longevity-cassandra-deps" % "0.6.0"
  // libraryDependencies += "org.longevityframework" %% "longevity-mongo-deps" % "0.6.0"

  // start building our subdomain:

  import longevity.subdomain.Assoc
  import longevity.subdomain.Entity
  import longevity.subdomain.EntityType
  import longevity.subdomain.EntityTypePool
  import longevity.subdomain.Shorthand
  import longevity.subdomain.ShorthandPool
  import longevity.subdomain.Subdomain
  import longevity.subdomain.persistent.Persistent
  import longevity.subdomain.persistent.Root
  import longevity.subdomain.ptype.RootType

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
    object keys {
      val uri = key(props.blog, props.uriPathSuffix)
    }
    object indexes {
    }
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
  import longevity.subdomain.Assoc
  import longevity.subdomain.persistent.Persistent
  import longevity.subdomain.ptype.KeyVal
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
    val createManyResult: Future[Seq[PState[_ <: Persistent]]] =
      repos.createMany(john, frank, blog, johnsPost, franksPost)

    // `futureValue` is a ScalaTest way of saying "wait for the future to
    // complete and assert success"
    createManyResult.futureValue

    // retrieve an entity:

    // `Repo[User].retrieve` returns a `Future[Option[PState[User]]]`,
    // aka `FOPState[User]`
    val retrieveResult: FOPState[User] =
      userRepo.retrieve(User.keys.username(john.username))

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

    val blogKeyVal: KeyVal[Blog] = Blog.keys.uri(blog.uri)
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
        User.keys.username(john.username)
      ).mapRoot(
        userService.updateUser _
      ).flatMapState(
        userRepo.update(_)
      )
    updated.futureValue

    val updatedReactive: FOPState[User] =
      userRepo.retrieve(
        User.keys.username(john.username)
      ).flatMapRoot(
        userService.updateUserReactive _
      ).flatMapState(
        userRepo.update(_)
      )
    updatedReactive.futureValue

    // equivalent to above, but without mapRoot and flatMapRoot

    val updatedNoMapRoot: FPState[User] = {
      userRepo retrieve User.keys.username(john.username) flatMap { optUserState =>
        val userState = optUserState getOrElse { throw new RuntimeException }
        val updatedState = userState map userService.updateUser
        userRepo update updatedState
      }
    }

    def applyEvent(username: String): FPState[User] = {
      userRepo retrieve User.keys.username(username) flatMap { optUserState =>
        val userState = optUserState getOrElse { throw new RuntimeException }
        val updatedState = userState map userService.updateUser
        userRepo update updatedState
      }
    }

    // use an `Assoc` to retrieve an author from a blog post:

    val post: BlogPost = blogPostRepo.retrieve(
      BlogPost.keys.uri(blogState.assoc, johnsPost.uriPathSuffix)
    ).futureValue.value.get
    val authorAssoc: Assoc[User] = post.authors.head
    val author: FPState[User] = userRepo.retrieveOne(authorAssoc)

    // find posts for a given blog published in the last week:

    import BlogPost.queryDsl._
    val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery(
      BlogPost.props.blog eqs blogState.assoc and
      BlogPost.props.postDate gt DateTime.now - 1.week)
    recentPosts.futureValue.size should equal (2)

    // same thing without the DSL:

    import longevity.subdomain.ptype.Query
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
    deletePost(johnsPost, blog)
    deletePost(franksPost, blog)
    deleteBlog(blog)
  }

  private def deleteUser(user: User): Unit = {
    val futureDeleted = for {
      optUserState <- userRepo retrieve User.keys.username(user.username)
      deleted <- optUserState map userRepo.delete getOrElse Future.successful(())
    } yield deleted
    futureDeleted.futureValue
  }

  private def deletePost(post: BlogPost, blog: Blog): Unit = {
    val deleted = for {
      optBlogState <- blogRepo retrieve Blog.keys.uri(blog.uri)
      optKeyVal = optBlogState map { blogState => BlogPost.keys.uri(blogState.assoc, post.uriPathSuffix) }
      optPostState <- optKeyVal map blogPostRepo.retrieve getOrElse Future.successful(None)
      deleted <- optPostState map blogPostRepo.delete getOrElse Future.successful(())
    } yield deleted
    deleted.futureValue
  }

  private def deleteBlog(blog: Blog): Unit = {
    val deleted = for {
      optBlogState <- blogRepo retrieve Blog.keys.uri(blog.uri)
      deleted <- optBlogState map blogRepo.delete getOrElse Future.successful(())
    } yield deleted
    deleted.futureValue
  }

}
```

