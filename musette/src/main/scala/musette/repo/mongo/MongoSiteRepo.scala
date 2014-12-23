package musette.repo.mongo

import reactivemongo.bson._
import longevity.repo._
import musette.domain._
import musette.repo.SiteRepo

class MongoSiteRepo(
  implicit override protected val repoPool: RepoPool
)
extends MongoRepo[Site](Site)
with SiteRepo {

  protected implicit val bsonHandler = Macros.handler[Site]

}
