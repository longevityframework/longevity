package longevity.unit.manual

import org.scalatest._
import scala.concurrent.ExecutionContext.Implicits.global

/** code samples found in the repo chapter of the user manual */
object RepoSpec {

  // used in http://longevityframework.github.io/longevity/manual/repo/retrieve-keyval.html
  object keyval {

    import longevity.integration.quickStart.QuickStartSpec._
    import longevity.persistence.PState

    def blogState: PState[Blog] = ???

    import longevity.subdomain.Assoc
    import longevity.subdomain.ptype.RootType
    import longevity.subdomain.ptype.Key
    import longevity.subdomain.ptype.KeyVal

    object User extends RootType[User] {
      object props {
        val username = prop[String]("username")
      }
      object keys {
        val username = key(props.username)
      }
      object indexes {
      }
    }

    val usernameKey: Key[User] = User.keys.username
    val username: String = "smithy"
    val usernameKeyVal: KeyVal[User] = usernameKey(username)

    object BlogPost extends RootType[BlogPost] {
      object props {
        val blog = prop[Assoc[Blog]]("blog")
        val suffix = prop[String]("uriPathSuffix")
      }
      object keys {
        val uri = key(props.blog, props.suffix)
      }
      object indexes {
      }
    }

    val blogAssoc: Assoc[Blog] = blogState.assoc
    val uriPathSuffix: String = "suffix"
    val blogPostKeyVal: KeyVal[BlogPost] = BlogPost.keys.uri(blogAssoc, uriPathSuffix)

  }

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

  "user manual example code" should "compile" in {

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
