package longevity.integration.noKeyspace

import longevity.config.Cassandra
import longevity.config.CassandraConfig
import longevity.config.LongevityConfig
import longevity.context.LongevityContext
import longevity.exceptions.persistence.cassandra.KeyspaceDoesNotExistException
import longevity.integration.model.basics
import longevity.test.LongevityFuturesSpec
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import scala.concurrent.ExecutionContext.{ global => globalExecutionContext }

/** integration test to make sure cassandra throws the right exception when
 * trying to use a repository and the keyspace hasnt been created yet
 */
class NoKeyspaceSpec extends FlatSpec with GivenWhenThen with LongevityFuturesSpec {

  override protected implicit val executionContext = globalExecutionContext

  val context = new LongevityContext[basics.DomainModel](
    LongevityConfig(
      backEnd = Cassandra,
      autoOpenConnection = false,
      autoCreateSchema = false,
      optimisticLocking = false,
      writeTimestamps = false,
      cassandra = CassandraConfig(
        keyspace = "no_such_keyspace",
        address = "127.0.0.1",
        credentials = None,
        replicationFactor = 1),
      mongodb = null,
      jdbc = null,
      test = null))

  behavior of "Cassandra Repo methods when the keyspace is not defined"

  it should "throw KeyspaceDoesNotExistException" in {
    val p = context.testDataGenerator.generate[basics.Basics]
    val repo = context.repo
    repo.openConnection.failed.futureValue shouldBe a [KeyspaceDoesNotExistException]
  }

}
