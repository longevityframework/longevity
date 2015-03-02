package musette.repo.mongo

import longevity.repo._
import musette.domain.Site
import musette.domain.SiteType
import musette.repo.SiteRepo

class MongoSiteRepo(implicit repoPool: OldRepoPool)
extends MusetteMongoRepo[Site](SiteType)
with SiteRepo
