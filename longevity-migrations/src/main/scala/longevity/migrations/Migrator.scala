package longevity.migrations

import cats.Cartesian
import cats.effect.IO
import cats.implicits._
import io.iteratee.Enumerator
import io.iteratee.Iteratee
import java.util.concurrent.Executors
import journal.Logger
import longevity.context.LongevityContext
import longevity.effect.cats.ioEffect
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.model.PEv
import longevity.model.query.{ FilterUnmigrated, Query }
import longevity.persistence.PState
import scala.concurrent.ExecutionContext

/** contains a method to apply the migration */
object Migrator {

  /** produces an IO for applying the migration */
  def migrate[M1, M2](migration: Migration[M1, M2]): IO[Unit] = {
    migration.validate.exception match {
      case Some(e) => IO.raiseError(e)
      case None => new Migrator(migration).migrate
    }
  }

}

private[longevity] class Migrator[M1, M2](migration: Migration[M1, M2]) {

  private val logger = Logger[this.type]

  private val nonBlockingThreadPool = Executors.newCachedThreadPool()
  private implicit val nonBlockingContext = ExecutionContext.fromExecutor(nonBlockingThreadPool)
  private val cartesion = implicitly[Cartesian[IO]]

  private implicit val modelType1 = migration.modelType1
  private implicit val modelType2 = migration.modelType2
  private[migrations] val context1 = LongevityContext[IO, M1](doctoredConfig1)
  private[migrations] val context2 = LongevityContext[IO, M2](doctoredConfig2)
  private val repo1 = context1.repo
  private val repo2 = context2.repo

  def migrate: IO[Unit] = {
    val open1 = repo1.openConnection
    val close1 = repo2.closeConnection
    val open2 = repo2.openConnection
    val close2 = repo1.closeConnection
    val body = for {
      _ <- cartesion.product(repo1.createMigrationSchema, repo2.createSchema)
      _ <- applyUpdates
      _ <- repo1.dropSchema
    } yield ()
    val body2 = for {
      _ <- open2
      _ <- body.handleErrorWith { case e => for { _ <- close2 } yield throw e }
      _ <- close2
    } yield ()
    val body1 = for {
      _ <- open1
      _ <- body2.handleErrorWith { case e => for { _ <- close1 } yield throw e }
      _ <- close1
    } yield ()
    body1
  }

  private def applyUpdates: IO[Unit] = {
    val asUpdateStep: PartialFunction[MigrationStep[M1, M2], UpdateStep[M1, M2, _, _]] = {
      case s: UpdateStep[_, _, _, _] => s
    }
    val updateIos = migration.steps.collect(asUpdateStep).map(updateStepToIo)
    updateIos.fold(IO.pure[Any](()))(cartesion.product(_, _)).map(_ => ())
  }

  private def updateStepToIo(step: UpdateStep[M1, M2, _, _]): IO[Unit] = updateStepToIoTyped(step)

  private def updateStepToIoTyped[P1, P2](step: UpdateStep[M1, M2, P1, P2]): IO[Unit] = {
    implicit val pEv1 = step.pEv1
    implicit val pEv2 = step.pEv2
    val e: IO[Enumerator[IO, PState[P1]]] =
      repo1.queryToIterateeIo[P1, IO](Query(filter = FilterUnmigrated()))
    val i: Iteratee[IO, PState[P1], Unit] = Iteratee.foreachM[IO, PState[P1]](updateP(_, step.f))
    e.flatMap(i(_).run)
  }

  private def updateP[P1 : PEv[M1, ?], P2 : PEv[M2, ?]](ps1: PState[P1], f: P1 => P2): IO[Unit] = {
    def startMigration =
      if (ps1.migrationStarted) IO.pure(()) else repo1.updateMigrationStarted(ps1)
    def createPs2(ps2: PState[P2], migrationStarted: Boolean) = try {
      repo2.createState(ps2)
    } catch {
      case e: DuplicateKeyValException[_, _] if migrationStarted =>
        logger.error(
          s"Got DuplicateKeyValException on key '${e.key}' migrating '${ps1.get}' to '${ps2.get}'. " +
            "This could indicate a problem with the migration, where two objects from the initial version " +
            "of the model wind up having a key collision in the final version of the model. However, there " +
            "was a failure migrating this object in an earlier run of the migrator, and it is possible that " +
            "the row was successfully migrated when that failure occurred. In this situation, we could also " +
            "see a DuplicateKeyValException here. We will continue on with the migration, assuming the " +
            "latter. But we advise that you check that the initial version was successfully migrated.")
        IO.pure(())
    }
    for {
      _ <- startMigration
      _ <- createPs2(ps1.map(f).unmigrate, ps1.migrationStarted)
      _ <- repo1.updateMigrationComplete(ps1)
    } yield ()
  }

  private def doctoredConfig1 = migration.config1.copy(
    modelVersion = migration.version1,
    autoOpenConnection = false,
    autoCreateSchema = false)

  private def doctoredConfig2 = migration.config2.copy(
    modelVersion = Some(migration.version2),
    autoOpenConnection = false,
    autoCreateSchema = false)

}
