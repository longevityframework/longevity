package longevity.unit.manual

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import scala.concurrent.ExecutionContext.Implicits.global

/** code samples found in the repo chapter of the user manual */
object RepoSpec {

  import org.joda.time.DateTime
  import longevity.model.KVType
  import longevity.model.ModelEv
  import longevity.model.ModelType
  import longevity.model.CType
  import longevity.model.PType

  trait BlogCore

  object BlogCore {
    implicit object modelType extends ModelType[BlogCore](
      Seq(User, Blog, BlogPost),
      Seq(CType[Markdown], CType[Uri], CType[UserProfile]))
    implicit object modelEv extends ModelEv[BlogCore]
  }

  case class Markdown(markdown: String)
  case class Uri(uri: String)

  case class Username(username: String)
  object Username extends KVType[BlogCore, User, Username]

  case class Email(email: String)
  object Email extends KVType[BlogCore, User, Email]

  case class User(
    username: Username,
    fullname: String,
    email: Email,
    profile: Option[UserProfile] = None) 

  object User extends PType[BlogCore, User] {
    object props {
      val username = prop[Username]("username")
      val email = prop[Email]("email")
    }
    val keySet = Set(key(props.username), key(props.email))
  }

  case class UserProfile(
    tagline: String,
    imageUri: Uri,
    description: Markdown) 

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
    val keySet = Set(key(props.uri))
  }

  case class BlogPostUri(uri: Uri)
  object BlogPostUri extends KVType[BlogCore, BlogPost, BlogPostUri]

  case class BlogPost(
    uri: BlogPostUri,
    title: String,
    slug: Option[Markdown] = None,
    content: Markdown,
    labels: Set[String] = Set(),
    postDate: DateTime,
    blog: BlogUri,
    authors: Set[Username]) 

  object BlogPost extends PType[BlogCore, BlogPost] {
    object props {
      val uri = prop[BlogPostUri]("uri")
      val blog = prop[BlogUri]("blog")
      val postDate = prop[DateTime]("postDate")
    }
    val keySet = Set(key(props.uri))
    override val indexSet = Set(index(props.blog, props.postDate))
  }

  import longevity.context.LongevityContext

  val context = LongevityContext[BlogCore]()

}

/** exercises code samples found in the repo chapter of the user manual.
 *
 * a couple of these are also duplicated in QuickStartSpec.scala
 *
 * @see http://longevityframework.github.io/longevity/manual/repo/
 */
class RepoSpec extends FlatSpec with GivenWhenThen with Matchers with LazyLogging {

  import RepoSpec._

  protected val repo = context.testRepo

  // used in http://longevityframework.github.io/longevity/manual/repo/query.html
  "retrieve by query example code" should "compile" in {

    if (false) { // don't run, just compile

      import com.github.nscala_time.time.Imports._
      import longevity.persistence.PState
      import longevity.model.query.Query
      import longevity.model.query.QueryFilter
      import scala.concurrent.Future

      val blog: Blog = ???

      val queryResult: Future[Seq[PState[BlogPost]]] = repo.queryToFutureVec(
        Query(
          QueryFilter.and(
            QueryFilter.eqs(BlogPost.props.blog, blog.uri),
            QueryFilter.gt(BlogPost.props.postDate, DateTime.now - 1.week))))
    }

    if (false) { // don't run, just compile

      import com.github.nscala_time.time.Imports._
      import longevity.persistence.PState
      import scala.concurrent.Future

      val blog: Blog = ???

      import BlogPost.queryDsl._
      val recentPosts: Future[Seq[PState[BlogPost]]] = repo.queryToFutureVec(
        BlogPost.props.blog eqs blog.uri and
        BlogPost.props.postDate gt DateTime.now - 1.week)
    }

    if (false) { // don't run, just compile

      import longevity.persistence.PState
      import scala.concurrent.Future

      val blog: Blog = ???

      val recentPosts: Future[Seq[PState[BlogPost]]] = repo.queryToFutureVec {
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

      val recentPosts: Source[PState[BlogPost], NotUsed] = repo.queryToAkkaStream {
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
