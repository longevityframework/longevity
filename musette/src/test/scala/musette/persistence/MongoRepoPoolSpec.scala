package musette.persistence

import musette.coredomain.longevityContext

import org.scalatest.Suites

class MongoRepoPoolSpec extends Suites(longevityContext.repoPoolSpec)
