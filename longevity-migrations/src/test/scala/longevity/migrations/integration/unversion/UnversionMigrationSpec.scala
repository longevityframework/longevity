package longevity.migrations.integration.unversion

import longevity.migrations.LongevityMigrationSpec

abstract class UnversionMigrationSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  behavior of "longevity.migrations.Unversioner.unversion"

  it should "perform basic create, drop, and update migration steps properly" in {

  }

}

