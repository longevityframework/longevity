package musette.repo.mongo

import reactivemongo.bson.Macros
import longevity.repo._
import musette.domain._
import musette.repo.CommentRepo

class MongoCommentRepo(implicit repoPool: RepoPool)
extends MusetteMongoRepo[Comment](Comment)
with CommentRepo
