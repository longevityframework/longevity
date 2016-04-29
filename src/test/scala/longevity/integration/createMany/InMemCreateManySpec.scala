package longevity.integration.createMany

import longevity.integration.subdomain.withAssoc

/** unit tests for the [[RepoPool.createMany]] method against in-memory back end */
class InMemCreateManySpec extends BaseCreateManySpec(
  withAssoc.context.mongoContext,
  withAssoc.context.mongoContext.testRepoPool)
