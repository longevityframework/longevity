package longevity.integration.writeConflict

import longevity.integration.subdomain.basics

/** optimistic locking tests for cassandra back end */
class CassandraOptLockSpec extends OptLockSpec(
  basics.cassandraContext,
  basics.cassandraContext.testRepoPool)
