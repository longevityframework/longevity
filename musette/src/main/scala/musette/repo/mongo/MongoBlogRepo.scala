package musette.repo.mongo

import reactivemongo.bson.Macros
import longevity.repo._
import musette.domain._
import musette.repo.BlogRepo

class MongoBlogRepo(implicit repoPool: RepoPool)
extends MusetteMongoRepo[Blog](Blog)
with BlogRepo
