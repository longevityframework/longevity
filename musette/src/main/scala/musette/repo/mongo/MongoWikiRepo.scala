package musette.repo.mongo

import longevity.domain._

import longevity.repo._
import musette.domain._
import musette.repo.WikiRepo

class MongoWikiRepo(implicit repoPool: RepoPool)
extends MusetteMongoRepo[Wiki](WikiType)
with WikiRepo
