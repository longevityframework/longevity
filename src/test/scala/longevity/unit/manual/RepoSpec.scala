package longevity.unit.manual

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import scala.concurrent.ExecutionContext.Implicits.global

/** code samples found in the repo chapter of the user manual */
object RepoSpec {

  import org.joda.time.DateTime
  import longevity.subdomain.KeyVal
  import longevity.subdomain.Subdomain
  import longevity.subdomain.Embeddable
  import longevity.subdomain.EType
  import longevity.subdomain.ETypePool
  import longevity.subdomain.Embeddable
  import longevity.subdomain.EType
  import longevity.ddd.subdomain.Root
  import longevity.subdomain.ptype.PTypePool
  import longevity.subdomain.ptype.RootType

  case class Markdown(markdown: String) extends Embeddable
  case class Uri(uri: String) extends Embeddable

  import longevity.subdomain.KeyVal

  case class Username(username: String)
  extends KeyVal[User, Username](User.keys.username)

  case class Email(email: String)
  extends KeyVal[User, Email](User.keys.email)

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

  val blogCore = Subdomain(
    "blogging",
    PTypePool(User, Blog, BlogPost),
    ETypePool(EType[Markdown], EType[Uri], EType[UserProfile]))

  import longevity.context.LongevityContext

  val context = LongevityContext(blogCore)

}

/** exercises code samples found in the repo chapter of the user manual.
 *
 * a couple of these are also duplicated in QuickStartSpec.scala
 *
 * @see http://longevityframework.github.io/longevity/manual/repo/
 */
class RepoSpec extends FlatSpec with GivenWhenThen with Matchers with LazyLogging {

  import RepoSpec._

  protected val repos = context.testRepoPool
  protected val userRepo = repos[User]
  protected val blogRepo = repos[Blog]
  protected val blogPostRepo = repos[BlogPost]

  // used in http://longevityframework.github.io/longevity/manual/repo/query.html
  "retrieve by query example code" should "compile" in {

    if (false) { // don't run, just compile

      import com.github.nscala_time.time.Imports._
      import longevity.persistence.PState
      import longevity.subdomain.ptype.Query
      import scala.concurrent.Future

      val blog: Blog = ???

      val queryResult: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery(
        Query.and(
          Query.eqs(BlogPost.props.blog, blog.uri),
          Query.gt(BlogPost.props.postDate, DateTime.now - 1.week)))
    }

    if (false) { // don't run, just compile

      import com.github.nscala_time.time.Imports._
      import longevity.persistence.PState
      import scala.concurrent.Future

      val blog: Blog = ???

      import BlogPost.queryDsl._
      val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery(
        BlogPost.props.blog eqs blog.uri and
        BlogPost.props.postDate gt DateTime.now - 1.week)
    }

    if (false) { // don't run, just compile

      import longevity.persistence.PState
      import scala.concurrent.Future

      val blog: Blog = ???

      val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery {
        import com.github.nscala_time.time.Imports._
        import BlogPost.queryDsl._
        import BlogPost.props
        props.blog eqs blog.uri and props.postDate gt DateTime.now - 1.week
      }
    }

  }

  // used in http://longevityframework.github.io/longevity/manual/repo/stream.html
  "stream by query example code" should "compile" in {

    if (false) { // don't run, just compile

      import akka.NotUsed
      import akka.stream.scaladsl.Source
      import longevity.persistence.PState

      val blog: Blog = ???

      val recentPosts: Source[PState[BlogPost], NotUsed] = blogPostRepo.streamByQuery {
        import com.github.nscala_time.time.Imports._
        import BlogPost.queryDsl._
        import BlogPost.props
        props.blog eqs blog.uri and props.postDate gt DateTime.now - 1.week
      }

      import akka.actor.ActorSystem
      import akka.stream.ActorMaterializer

      implicit val system = ActorSystem("blogging")
      implicit val materializer = ActorMaterializer()

      recentPosts.runForeach { blogPostState => logger.debug(s"query returned ${blogPostState.get}") }

      import akka.stream.scaladsl.Sink

      recentPosts.to(Sink.foreach { state => logger.debug(s"query returned ${state.get}") })

      recentPosts.map(_.get).runForeach {
        post: BlogPost => logger.debug(s"query returned ${post}")
      }

      import akka.stream.scaladsl.Keep
      import scala.concurrent.Future

      val numRecentPosts: Future[Int] =
        recentPosts.map(_ => 1).toMat(Sink.reduce[Int](_ + _))(Keep.right).run()

    }

  }

}
