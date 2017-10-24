package longevity.migrations.integration.failures

import cats.implicits._
import longevity.migrations.integration.basic.BaseBasicMigrationSpec
import scala.concurrent.Await
import scala.concurrent.duration.Duration

abstract class FailuresMigrationSpec extends BaseBasicMigrationSpec {

  override protected def keyspace = "longevity_migrations_test_failures"
  lazy val migration = migrationForConfig(config)

  behavior of "longevity.migrations.Migrator.migrate"

  it should "be able to pick up where a previous, interrupted migration left off" in {

    // there are 100 users, and intermittent failures every 40 rows. so we should encounter exactly
    // 2 intermittent failures on repeated migration attempts

    val f = for {
      initialUsers <- setup(testData)
      attempt1     <- migrator.migrate.attempt
      _            = attempt1 should equal (Left(IntermittentFailure))
      attempt2     <- migrator.migrate.attempt
      _            = attempt2 should equal (Left(IntermittentFailure))
      _            <- migrator.migrate
      test         <- results(initialUsers)
    } yield test

    Await.result(f, Duration.Inf)
  }

}

