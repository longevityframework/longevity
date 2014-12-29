package musette.repo.mongo

import reactivemongo.bson.Macros

import scala.reflect.runtime.universe.TypeTag
import reactivemongo.bson._
import longevity.domain._

import longevity.repo._
import musette.domain._
import musette.repo.WikiRepo

class MongoWikiRepo(
  implicit override protected val repoPool: RepoPool
)
extends MongoRepo[Wiki](Wiki)
with WikiRepo {

  private implicit val siteHandler = assocHandler[Site]
  private implicit val userHandler = assocHandler[User]
  protected implicit val bsonHandler = Macros.handler[Wiki]

}

