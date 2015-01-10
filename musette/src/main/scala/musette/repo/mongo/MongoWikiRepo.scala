package musette.repo.mongo

import reactivemongo.bson.Macros

import scala.reflect.runtime.universe.TypeTag
import reactivemongo.bson._
import longevity.domain._

import longevity.repo._
import musette.domain._
import musette.repo.WikiRepo

class MongoWikiRepo(implicit repoPool: RepoPool)
extends MusetteMongoRepo[Wiki](Wiki)
with WikiRepo
