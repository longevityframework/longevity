package longevity.migrations.integration.basic

import scala.concurrent.Await
import scala.concurrent.duration.Duration

abstract class BasicMigrationSpec extends BaseBasicMigrationSpec {

  lazy val migration = migrationForConfig(config)

  behavior of "longevity.migrations.Migrator.migrate"

  it should "perform basic create, drop, and update migration steps properly" in {

    val f = for {
      initialUsers <- setup(testData)
      _            <- migrator.migrate
      test         <- results(initialUsers)
    } yield test

    Await.result(f, Duration.Inf)
  }

}

