package longevity.integration.createMany

import longevity.context.LongevityContext
import longevity.exceptions.subdomain.AssocIsUnpersistedException
import longevity.integration.subdomain.withAssoc
import longevity.persistence.RepoPool
import longevity.subdomain.Root

/** unit tests for the [[RepoPoolSpec.createMany]] method */
class CreateManySpec extends BaseCreateManySpec(withAssoc.mongoContext) {

  def uri = testDataGenerator.generate[String]

  behavior of "RepoPool.createMany"

  // TODO need more tests that show out the cache actually does get updated appropraitely

  it should "persist networks with unpersisted associations" in {
    val associated1 = withAssoc.Associated(uri)
    val withAssoc1 = withAssoc.WithAssoc(uri, associated1)
    val associated2 = withAssoc.Associated(uri)
    val withAssoc2 = withAssoc.WithAssoc(uri, associated2)

    val repoPool = withAssoc.mongoContext.testRepoPool
    import longevity.persistence.rootWithTypeKey
    val result = repoPool.createMany(associated1, withAssoc1, associated2, withAssoc2)
    val pstates = result.futureValue
    pstates.size should equal(4)
    persistedShouldMatchUnpersisted(pstates(0).get.asInstanceOf[withAssoc.Associated], associated1)
    persistedShouldMatchUnpersisted(pstates(1).get.asInstanceOf[withAssoc.WithAssoc], withAssoc1)
    persistedShouldMatchUnpersisted(pstates(2).get.asInstanceOf[withAssoc.Associated], associated2)
    persistedShouldMatchUnpersisted(pstates(3).get.asInstanceOf[withAssoc.WithAssoc], withAssoc2)
  }

  behavior of "MongoRepo.create"

  it should "throw AssocIsUnpersistedException when the aggregate contains unpersisted assocs" in {
    val associated1 = withAssoc.Associated(uri)
    val withAssoc1 = withAssoc.WithAssoc(uri, associated1)
    val repoPool = withAssoc.mongoContext.testRepoPool
    val withAssocRepo = repoPool[withAssoc.WithAssoc]
    withAssocRepo.create(withAssoc1).failed.futureValue shouldBe an [AssocIsUnpersistedException]
  }

  behavior of "InMemRepo.create"

  // this exposes a bug pt #110719144
  ignore should "throw AssocIsUnpersistedException when the aggregate contains unpersisted assocs" in {
    val associated1 = withAssoc.Associated(uri)
    val withAssoc1 = withAssoc.WithAssoc(uri, associated1)
    val repoPool = withAssoc.mongoContext.inMemTestRepoPool
    val withAssocRepo = repoPool[withAssoc.WithAssoc]
    withAssocRepo.create(withAssoc1).failed.futureValue shouldBe an [AssocIsUnpersistedException]
  }

}
