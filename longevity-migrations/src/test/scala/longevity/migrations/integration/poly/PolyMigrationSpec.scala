package longevity.migrations.integration.poly

abstract class PolyMigrationSpec extends BasePolyMigrationSpec {

  lazy val migration = migrationForConfig(config)

  behavior of "longevity.migrations.Migrator.migrate"

  it should "perform poly create, drop, and update migration steps properly" in {

    val testIo = for {
      initialUsers <- setup(testData)
      _            <- migrator.migrate
      test         <- results(initialUsers)
    } yield test

    testIo.unsafeRunSync()
  }

}

