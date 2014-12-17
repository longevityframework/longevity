package longevity.repo

trait BaseRepoLayer {
  implicit protected val repoPool = new RepoPool
}
