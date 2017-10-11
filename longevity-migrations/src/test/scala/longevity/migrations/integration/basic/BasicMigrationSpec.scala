package longevity.migrations.integration.basic

abstract class BasicMigrationSpec extends BaseBasicMigrationSpec {

  lazy val migration = migrationForConfig(config)

  behavior of "longevity.migrations.Migrator.migrate"

  it should "perform basic create, drop, and update migration steps properly" in {

    val testIo = for {
      initialUsers <- setup(testData)
      _            <- migrator.migrate
      test         <- results(initialUsers)
    } yield test

    testIo.unsafeRunSync()
  }

}

