package longevity.integration.poly

import longevity.integration.subdomain.derivedEntities

/** tests for mongo repos that share tables in the presence of [[PolyType]] */
class MongoPolyReposSpec extends PolyReposSpec(
  derivedEntities.context.mongoContext,
  derivedEntities.context.mongoContext.testRepoPool)
