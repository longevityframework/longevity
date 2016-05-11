package longevity.integration.poly

import longevity.integration.subdomain.derivedEntities

/** tests for in-memory repos that share tables in the presence of [[PolyType]] */
class InMemPolyReposSpec extends PolyReposSpec(
  derivedEntities.context.mongoContext,
  derivedEntities.context.mongoContext.inMemTestRepoPool)
