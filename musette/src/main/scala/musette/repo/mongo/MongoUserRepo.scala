package musette.repo.mongo

import reactivemongo.bson.Macros
import longevity.repo._
import musette.domain._
import musette.repo.UserRepo

class MongoUserRepo(
  implicit override protected val repoPool: RepoPool
)
extends MongoRepo[User](User)
with UserRepo {

  private implicit val siteHandler = assocHandler[Site]
  protected implicit val bsonHandler = Macros.handler[User]

}

