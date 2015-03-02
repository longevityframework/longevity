package longevity.integration.master

import longevity.repo._
import longevity.integration.master.domain._

object repo {

  trait RepoLayer extends BaseRepoLayer {
    val userRepo: Repo[User]
  }

  trait InMemRepoLayer extends RepoLayer {
    val userRepo = new InMemRepo[User](UserType)
  }

  trait MongoRepoLayer extends RepoLayer {
    val userRepo = new MongoRepo[User](UserType)
  }

}
