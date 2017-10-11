package longevity.migrations.integration.poly

import longevity.config.InMem

class InMemPolyMigrationSpec extends PolyMigrationSpec {
  protected def backEnd = InMem
}
