package longevity.migrations.integration.basic

import longevity.config.InMem

class InMemBasicMigrationSpec extends BasicMigrationSpec {
  protected def backEnd = InMem
}
