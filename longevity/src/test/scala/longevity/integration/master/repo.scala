package longevity.integration.master

import longevity.repo._
import longevity.integration.master.domain._

// TODO:
// - s/tag/key/
// - RepoPoolBuilder
//   - redo Repo.repoPool
//   - change RepoPool to TypeKeyMap[]
//   - change RepoPoolSpec into RepoPoolBuilderSpec
// - repo.inMemRepoPoolForBoundedCountext
// - repo.mongoRepoPoolForBoundedCountext
// - specs for these two
// - RepoPoolSpec(2)

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
