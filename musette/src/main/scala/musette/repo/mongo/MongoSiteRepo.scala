package musette.repo.mongo

import longevity.repo._
import musette.domain.Site
import musette.domain.SiteType
import musette.repo.SiteRepo

class MongoSiteRepo(implicit repoPool: RepoPool)
extends MusetteMongoRepo[Site](SiteType)
with SiteRepo
