package musette.repo.mongo

import longevity.repo._
import musette.domain.Blog
import musette.domain.BlogType
import musette.repo.BlogRepo

class MongoBlogRepo(implicit repoPool: OldRepoPool)
extends MusetteMongoRepo[Blog](BlogType)
with BlogRepo
