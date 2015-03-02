package musette.repo.mongo

import longevity.repo._
import musette.domain.Comment
import musette.domain.CommentType
import musette.repo.CommentRepo

class MongoCommentRepo(implicit repoPool: OldRepoPool)
extends MusetteMongoRepo[Comment](CommentType)
with CommentRepo
