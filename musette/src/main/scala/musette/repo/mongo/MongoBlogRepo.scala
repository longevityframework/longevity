package musette.repo.mongo

import longevity.repo._
import musette.domain.Blog
import musette.domain.BlogType
import musette.repo.BlogRepo

class MongoBlogRepo(implicit repoPool: RepoPool)
extends MusetteMongoRepo[Blog](BlogType)
with BlogRepo
