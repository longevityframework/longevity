package longevity.integration.createMany

import longevity.integration.subdomain.withAssoc
import longevity.context.LongevityContext
import longevity.persistence.RepoPool
import longevity.subdomain.Root

/** unit tests for the [[RepoPoolSpec.createMany]] method */
class CreateManySpec extends BaseCreateManySpec(withAssoc.mongoContext) {

  behavior of "RepoPool.createMany"

  it should "persist networks with unpersisted associations" in {

    def uri = testDataGenerator.generate[String]

    val associated1 = withAssoc.Associated(uri)
    val withAssoc1 = withAssoc.WithAssoc(uri, associated1)
    val associated2 = withAssoc.Associated(uri)
    val withAssoc2 = withAssoc.WithAssoc(uri, associated2)

    val repoPool = withAssoc.mongoContext.inMemTestRepoPool

    import longevity.persistence.rootWithTypeKey
    val result = repoPool.createMany(associated1, withAssoc1, associated2, withAssoc2)
    val pstates = result.futureValue
    pstates.size should equal(4)
    persistedShouldMatchUnpersisted(pstates(0).get.asInstanceOf[withAssoc.Associated], associated1)
    persistedShouldMatchUnpersisted(pstates(1).get.asInstanceOf[withAssoc.WithAssoc], withAssoc1)
    persistedShouldMatchUnpersisted(pstates(2).get.asInstanceOf[withAssoc.Associated], associated2)
    persistedShouldMatchUnpersisted(pstates(3).get.asInstanceOf[withAssoc.WithAssoc], withAssoc2)
  }

}
