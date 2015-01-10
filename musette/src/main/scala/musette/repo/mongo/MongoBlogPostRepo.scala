package musette.repo.mongo

import reactivemongo.bson.Macros
import longevity.repo._
import musette.domain._
import musette.repo.BlogPostRepo

class MongoBlogPostRepo(implicit repoPool: RepoPool)
extends MusetteMongoRepo[BlogPost](BlogPost)
with BlogPostRepo
