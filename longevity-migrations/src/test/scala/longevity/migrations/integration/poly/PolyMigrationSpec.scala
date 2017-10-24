package longevity.migrations.integration.poly

import scala.concurrent.Await
import scala.concurrent.duration.Duration

abstract class PolyMigrationSpec extends BasePolyMigrationSpec {

  lazy val migration = migrationForConfig(config)

  behavior of "longevity.migrations.Migrator.migrate"

  it should "perform poly create, drop, and update migration steps properly" in {
    val f = for {
      initialUsers <- setup(testData)
      _            <- migrator.migrate
      test         <- results(initialUsers)
    } yield test

    Await.result(f, Duration.Inf)
  }

}
