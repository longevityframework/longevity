package longevity.integration.writeConflict

import longevity.integration.subdomain.basics

/** optimistic locking tests for inmem back end */
class InMemOptLockSpec extends OptLockSpec(basics.mongoContext, basics.mongoContext.inMemTestRepoPool)
