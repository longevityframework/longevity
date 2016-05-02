package longevity.integration.poly

import longevity.integration.subdomain.derivedEntities

/** base class for test repos that share tables in the presence of [[PolyType]] */
class CassandraPolyReposSpec extends PolyReposSpec(
  derivedEntities.context.cassandraContext,
  derivedEntities.context.cassandraContext.testRepoPool)
