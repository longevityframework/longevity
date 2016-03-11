package longevity.unit.manual

import org.scalatest._
import scala.concurrent.ExecutionContext.Implicits.global

/** exercises code samples found in the repo.retrieveByQuery section
 * of the user manual. the samples themselves are in [[RootTypeSpec]]
 * companion object.
 *
 * a couple of these are also duplicated in QuickStartSpec.scala
 *
 * @see http://longevityframework.github.io/longevity/manual/context/repo-query.html
 */
class RepoQuerySpec extends FlatSpec with GivenWhenThen with Matchers {

  import longevity.integration.quickStart.QuickStartSpec._

  protected val repos = context.testRepoPool
  protected val userRepo = repos[User]
  protected val blogRepo = repos[Blog]
  protected val blogPostRepo = repos[BlogPost]

  "user manual example code" should "produce correct queries" in {

    // duplicated at https://gist.github.com/sullivan-/fa2001b32ea19084a3d0
    if (false) { // don't run, just compile

      import com.github.nscala_time.time.Imports._
      import longevity.persistence.PState
      import longevity.subdomain.ptype.Query
      import scala.concurrent.Future

      def getBlogState(): PState[Blog] = ???
      val blogState: PState[Blog] = getBlogState()

      val queryResult: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery(
        Query.and(
          Query.eqs(BlogPost.props.blog, blogState.assoc),
          Query.gt(BlogPost.props.postDate, DateTime.now - 1.week)))
    }
 
    // duplicated at https://gist.github.com/sullivan-/42caecbb5b2096afd4a8
    if (false) { // don't run, just compile

      import com.github.nscala_time.time.Imports._
      import longevity.persistence.PState
      import scala.concurrent.Future

      def getBlogState(): PState[Blog] = ???
      val blogState: PState[Blog] = getBlogState()

      import BlogPost.queryDsl._
      val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery(
        BlogPost.props.blog eqs blogState.assoc and
        BlogPost.props.postDate gt DateTime.now - 1.week)
    }
    // duplicated at https://gist.github.com/sullivan-/ca39baf6637037529421
    if (false) { // don't run, just compile

      import com.github.nscala_time.time.Imports._
      import longevity.persistence.PState
      import scala.concurrent.Future

      def getBlogState(): PState[Blog] = ???
      val blogState: PState[Blog] = getBlogState()

      val recentPosts: Future[Seq[PState[BlogPost]]] = blogPostRepo.retrieveByQuery {
        import BlogPost.queryDsl._
        import BlogPost.props._
        blog eqs blogState.assoc and postDate gt DateTime.now - 1.week
      }
    }

  }

}
