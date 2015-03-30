package musette.persistence

import musette.coredomain.longevityContext
import longevity.test.ScalaTestSpecs
import org.scalatest.Suites

class InMemRepoPoolSpec extends Suites(longevityContext.inMemRepoPoolSpec)
