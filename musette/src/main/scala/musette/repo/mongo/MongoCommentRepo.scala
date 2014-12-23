package musette.repo.mongo

import reactivemongo.bson.Macros
import longevity.repo._
import musette.domain._
import musette.repo.CommentRepo

class MongoCommentRepo(
  implicit override protected val repoPool: RepoPool
)
extends MongoRepo[Comment](Comment)
with CommentRepo {

  // TODO: improve pattern for generating bson handler
  private implicit val blogPostHandler = assocHandler[BlogPost]
  private implicit val userHandler = assocHandler[User]
  protected implicit val bsonHandler = Macros.handler[Comment]

}
