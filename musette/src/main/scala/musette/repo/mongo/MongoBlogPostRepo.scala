package musette.repo.mongo

import longevity.repo._
import musette.domain.BlogPost
import musette.domain.BlogPostType
import musette.repo.BlogPostRepo

class MongoBlogPostRepo(implicit repoPool: RepoPool)
extends MusetteMongoRepo[BlogPost](BlogPostType)
with BlogPostRepo
