package longevity.migrations.unit

import longevity.migrations.Migrator
import org.scalatest.FlatSpec
import org.scalatest.Matchers

/** unit tests for the proper behavior of static method `Migrator.lookupMigration` */
class LookupMigrationSpec extends FlatSpec with Matchers {

  behavior of "Migrator.lookupMigration"

  it should "find a migration by name in the given package" in {
    Migrator.lookupMigration("longevity.migrations.somePackage", "basicMigration") should equal {
      longevity.migrations.somePackage.basicMigration
    }
  }

}
