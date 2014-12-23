package musette.repo.mongo

import musette.repo.RepoLayer

class MongoRepoLayer extends RepoLayer {
  override val blogRepo = new MongoBlogRepo
  override val blogPostRepo = new MongoBlogPostRepo
  override val commentRepo = new MongoCommentRepo
  override val siteRepo = new MongoSiteRepo
  override val userRepo = new MongoUserRepo
  override val wikiRepo = new MongoWikiRepo
  override val wikiPageRepo = new MongoWikiPageRepo
}
