package musette.repo.mongo

import reactivemongo.bson.Macros
import longevity.repo._
import musette.domain._
import musette.repo.UserRepo

class MongoUserRepo(implicit repoPool: RepoPool)
extends MusetteMongoRepo[User](User)
with UserRepo
