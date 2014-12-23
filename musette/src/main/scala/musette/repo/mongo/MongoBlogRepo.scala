package musette.repo.mongo

import reactivemongo.bson.Macros
import longevity.repo._
import musette.domain._
import musette.repo.BlogRepo

class MongoBlogRepo(
  implicit override protected val repoPool: RepoPool
)
extends MongoRepo[Blog](Blog)
with BlogRepo {

  private implicit val siteHandler = assocHandler[Site]
  private implicit val userHandler = assocHandler[User]
  protected implicit val bsonHandler = Macros.handler[Blog]

}
