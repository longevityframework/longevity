package longevity.integration.writeConflict

import longevity.integration.subdomain.basics

/** optimistic locking tests for mongo back end */
class MongoOptLockSpec extends OptLockSpec(basics.mongoContext, basics.mongoContext.testRepoPool)
