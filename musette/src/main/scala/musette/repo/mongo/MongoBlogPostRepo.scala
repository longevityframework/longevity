package musette.repo.mongo

import reactivemongo.bson.Macros
import longevity.repo._
import musette.domain._
import musette.repo.BlogPostRepo

class MongoBlogPostRepo(
  implicit override protected val repoPool: RepoPool
)
extends MongoRepo[BlogPost](BlogPost)
with BlogPostRepo {

  import musette.domain._
  private implicit val blogHandler = assocHandler[Blog]
  private implicit val userHandler = assocHandler[User]
  protected implicit val bsonHandler = Macros.handler[BlogPost]

}
