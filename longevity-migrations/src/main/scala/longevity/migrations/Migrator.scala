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
import scala.io.StdIn.readLine
import scala.reflect.runtime.{ universe => ru }

/** an application to apply a migration.
 *
 * expects arguments like so: `<migrationsPackage> <migrationName> <flags>`
 *
 * currently supported flags are: `--nonInteractive`
 */
object Migrator extends App {

  val (migrationsPackage, migrationName, flags) = processArgs(args)
  val migration = lookupMigration(migrationsPackage, migrationName)
  val validationResult = migration.validate
  if (!validationResult.isValid) {
    throw new RuntimeException(
      s"""|The following validation errors were encountered in migration '$migrationName':
          |\n  - ${validationResult.errors.map(_.message).mkString("\n  - ")}""".stripMargin)
  }

  if (migrationConfirmed(flags)) {
    def migrator[M1, M2](migration: Migration[M1, M2]) = new Migrator(migration)
    val m = migrator(migration)
    m.migrate.unsafeRunSync
    println
    println(s"""|Migration completed successfully. Please remember to update your configuration to
                |set `longevity.modelVersion` to `${migration.version2}`.
                |
                |Have a nice rest of your day.""".stripMargin)
  }
  sys.exit(0)

  private def processArgs(args: Array[String]) = {
    val migrationsPackage = args(0)
    val (flags, migrationNames) = args.tail.partition(_.startsWith("--"))
    if (migrationNames.size == 0) {
      throw new RuntimeException("migration name argument was not supplied")
    }
    if (migrationNames.size > 1) {
      throw new RuntimeException(s"multiple migration name arguments supplied: ${migrationNames.mkString(", ")}")
    }
    val migrationName = migrationNames(0)
    (migrationsPackage, migrationName, flags)
  }

  private def migrationConfirmed(flags: Seq[String]) =
    flags.contains("--nonInteractive") ||
      (userConfirms(backupConfirmation) && userConfirms(serverShutdownConfirmation))

  private def backupConfirmation =
    s"""|
        |We strongly recommend that you back up your data before running longevity migrations. The migrations
        |interface is still experimental, and as with any schema migration system, unanticipated things can
        |happen that can result in corrupted data.
        |
        |Have you backed up your data, or are you otherwise willing to accept the risks (y/N)? """.stripMargin

  private def serverShutdownConfirmation =
    s"""|
        |Please shut down any servers or other applications that may be using longevity against the initial
        |schema before running the migration. Any writes through to the initial schema may be lost. The
        |initial schema is also dropped as one of the last steps of migration, so even read-only applications
        |will be interrupted in this process. (We plan to add a --maintainInitialSchema flag to allow for
        |read-only applications to work through the migration, but it's not in place yet.)
        |
        |Have you shut down any applications using longevity against this domain (y/N)? """.stripMargin

  private def userConfirms(message: String): Boolean = {
    print(message)
    val response = readLine()
    response == "y" || response == "Y" || response == "yes" || response == "Yes"
  }

  // exposed for unit testing
  private[migrations] def lookupMigration(migrationsPackage: String, migrationName: String): Migration[_, _] = {
    val runtimeMirror  = ru.runtimeMirror(getClass.getClassLoader)
    val moduleSymbol   = runtimeMirror.staticModule(s"$migrationsPackage.package$$")
    val moduleMirror   = runtimeMirror.reflectModule(moduleSymbol)
    val moduleInstance = moduleMirror.instance
    val instanceMirror = runtimeMirror.reflect(moduleInstance)
    val fieldSymbol    = instanceMirror.symbol.info.member(ru.TermName(migrationName)).asTerm.accessed.asTerm
    val fieldMirror    = instanceMirror.reflectField(fieldSymbol)
    fieldMirror.get.asInstanceOf[Migration[_, _]]
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
      _ <- repo1.createMigrationSchema.product(repo2.createSchema)
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
