package longevity.migrations.integration.failures

import longevity.config.InMem

class InMemFailuresMigrationSpec extends FailuresMigrationSpec {
  protected def backEnd = InMem
}
