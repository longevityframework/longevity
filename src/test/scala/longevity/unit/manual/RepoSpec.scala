package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import scala.concurrent.ExecutionContext.Implicits.global

/** code samples found in the repo chapter of the user manual */
object RepoSpec {

}

/** exercises code samples found in the repo chapter of the user manual.
 *
 * a couple of these are also duplicated in QuickStartSpec.scala
 *
 * @see http://longevityframework.github.io/longevity/manual/repo/
 */
class RepoSpec extends FlatSpec with GivenWhenThen with Matchers {

  import longevity.integration.quickStart.QuickStartSpec._

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

      recentPosts.runForeach { blogPostState => println(s"query returned ${blogPostState.get}") }

      import akka.stream.scaladsl.Sink

      recentPosts.to(Sink.foreach { state => println(s"query returned ${state.get}") })

      recentPosts.map(_.get).runForeach {
        post: BlogPost => println(s"query returned ${post}")
      }

      import akka.stream.scaladsl.Keep
      import scala.concurrent.Future

      val numRecentPosts: Future[Int] =
        recentPosts.map(_ => 1).toMat(Sink.reduce[Int](_ + _))(Keep.right).run()

    }

  }

}
