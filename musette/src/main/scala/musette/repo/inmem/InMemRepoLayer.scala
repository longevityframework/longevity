package musette.repo.inmem

import musette.repo.RepoLayer

trait InMemRepoLayer extends RepoLayer {
  override val blogRepo = new InMemBlogRepo
  override val blogPostRepo = new InMemBlogPostRepo
  override val commentRepo = new InMemCommentRepo
  override val userRepo = new InMemUserRepo
  override val wikiRepo = new InMemWikiRepo
  override val wikiPageRepo = new InMemWikiPageRepo
}
