package musette.repo

import longevity.repo.BaseRepoLayer

trait RepoLayer extends BaseRepoLayer {
  val blogRepo: BlogRepo
  val blogPostRepo: BlogPostRepo
  val commentRepo: CommentRepo
  val siteRepo: SiteRepo
  val userRepo: UserRepo
  val wikiRepo: WikiRepo
  val wikiPageRepo: WikiPageRepo
}
