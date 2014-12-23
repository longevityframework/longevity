package musette.repo.mongo

import reactivemongo.bson.Macros
import longevity.repo._
import musette.domain._
import musette.repo.WikiPageRepo

class MongoWikiPageRepo(
  implicit override protected val repoPool: RepoPool
)
extends MongoRepo[WikiPage](WikiPage)
with WikiPageRepo {

  private implicit val userHandler = assocHandler[User]
  private implicit val wikiHandler = assocHandler[Wiki]
  protected implicit val bsonHandler = Macros.handler[WikiPage]

}

