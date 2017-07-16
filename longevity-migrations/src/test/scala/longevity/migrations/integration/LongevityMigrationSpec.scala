package longevity.migrations.integration

import cats.implicits._
import java.util.concurrent.Executors
import journal.Logger
import longevity.config.BackEnd
import longevity.migrations.{ Migration, Migrator }
import longevity.model.PEv
import longevity.persistence.PState
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

/** common code for longevity migration specs.
 *
 * note these tests happen in the main repos, not the test repos, as the migrator was designed to work
 * on main repo.
 *
 * @tparam M1 the initial model
 * @tparam M2 the final model
 */
abstract class LongevityMigrationSpec[M1, M2] extends FlatSpec with Matchers with BeforeAndAfterAll {

  protected val logger = Logger[this.type]

  protected def backEnd: BackEnd
  protected def keyspace: String
  protected def config = MigrationConfig.config(backEnd, keyspace)
  protected def migration: Migration[M1, M2]

  private val nonBlockingThreadPool = Executors.newCachedThreadPool()
  protected implicit val nonBlockingContext = ExecutionContext.fromExecutor(nonBlockingThreadPool)
  private implicit val modelType1 = migration.modelType1
  private implicit val modelType2 = migration.modelType2
  protected[migrations] val migrator = new Migrator(migration)
  protected val context1 = migrator.context1
  protected val context2 = migrator.context2

  protected def setup[A](testDataFuture: => Future[A]) = for {
    _        <- context1.repo.openConnection
    _        <- context1.repo.createSchema
    testData <- testDataFuture
    _        <- context1.repo.closeConnection
  } yield testData

  protected def createTestData[P1 : PEv[M1, ?]](numPersistents: Int): Future[Vector[PState[P1]]] = {
    def createP = {
      val p = context1.testDataGenerator.generateP[P1]
      context1.repo.create(p)
    }
    Vector.fill(numPersistents)(createP).sequence
  }

}
