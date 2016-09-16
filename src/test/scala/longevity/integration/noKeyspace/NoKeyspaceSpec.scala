package longevity.integration.noKeyspace

import emblem.emblematic.traversors.sync.CustomGeneratorPool
import longevity.context.Cassandra
import longevity.context.CassandraConfig
import longevity.context.LongevityConfig
import longevity.context.LongevityContext
import longevity.exceptions.persistence.cassandra.KeyspaceDoesNotExistException
import longevity.integration.subdomain.basics
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import scala.concurrent.ExecutionContext.Implicits.global

/** integration test to make sure cassandra throws the right exception when
 * trying to use a repository and the keyspace hasnt been created yet
 */
class NoKeyspaceSpec extends FlatSpec with GivenWhenThen with Matchers with ScalaFutures {

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(4000.millis),
    interval = scaled(50.millis))

  val context = new LongevityContext(
    basics.subdomain,
    Cassandra,
    CustomGeneratorPool.empty,
    LongevityConfig(
      autocreateSchema = false,
      optimisticLocking = false,
      mongodb = null,
      cassandra = CassandraConfig(
        keyspace = "no_such_keyspace",
        address = "127.0.0.1",
        credentials = None,
        replicationFactor = 1),
      test = null))

  behavior of "Cassandra Repo methods when the keyspace is not defined"

  it should "throw KeyspaceDoesNotExistException" in {
    val p = context.testDataGenerator.generate[basics.Basics]
    val createResult = context.repoPool[basics.Basics].create(p)
    createResult.failed.futureValue shouldBe a [KeyspaceDoesNotExistException]
  }

}
