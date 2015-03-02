package musette.repo.mongo

import longevity.repo._
import musette.domain._
import musette.repo.WikiRepo

class MongoWikiRepo(implicit repoPool: OldRepoPool)
extends MusetteMongoRepo[Wiki](WikiType)
with WikiRepo
