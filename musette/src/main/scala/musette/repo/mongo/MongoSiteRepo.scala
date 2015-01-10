package musette.repo.mongo

import longevity.repo._
import musette.domain.Site
import musette.repo.SiteRepo

class MongoSiteRepo(implicit repoPool: RepoPool)
extends MusetteMongoRepo[Site](Site)
with SiteRepo
