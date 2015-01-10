package musette.repo.mongo

import reactivemongo.bson.Macros
import longevity.repo._
import musette.domain._
import musette.repo.WikiPageRepo

class MongoWikiPageRepo(implicit repoPool: RepoPool)
extends MusetteMongoRepo[WikiPage](WikiPage)
with WikiPageRepo
