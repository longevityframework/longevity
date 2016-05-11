package longevity.integration.poly

import longevity.integration.subdomain.derivedEntities

/** tests for cassandra repos that share tables in the presence of [[PolyType]] */
class CassandraPolyReposSpec extends PolyReposSpec(
  derivedEntities.context.cassandraContext,
  derivedEntities.context.cassandraContext.testRepoPool)
