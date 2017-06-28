package longevity.integration.duplicateKeyVal

import longevity.TestLongevityConfigs
import longevity.context.Effect
import longevity.context.LongevityContext
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.integration.model.primaryKey.Key
import longevity.integration.model.primaryKey.PrimaryKey
import longevity.integration.model.primaryKey.DomainModel
import longevity.persistence.Repo
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** expect all back ends to throw DuplicateKeyValException on a duplicate
 * primary key value. except cassandra of course. in cassandra, if you insert
 * a duplicate primary key, it will simply overwrite the old row.
 */
class DuplicatePrimaryKeyValSpec extends FlatSpec with BeforeAndAfterAll with Matchers {

  val inMemContext = new LongevityContext[Future, DomainModel](TestLongevityConfigs.inMemConfig)
  val mongoContext = new LongevityContext[Future, DomainModel](TestLongevityConfigs.mongoConfig)
  val sqliteContext = new LongevityContext[Future, DomainModel](TestLongevityConfigs.sqliteConfig)

  override def beforeAll() = {
    inMemContext.effect.run(inMemContext.testRepo.createSchema)
    mongoContext.effect.run(mongoContext.testRepo.createSchema)
    sqliteContext.effect.run(sqliteContext.testRepo.createSchema)
  }

  assertDuplicateKeyValBehavior(inMemContext.effect, inMemContext.testRepo, "InMemPRepo")
  assertDuplicateKeyValBehavior(mongoContext.effect, mongoContext.testRepo, "MongoPRepo")
  assertDuplicateKeyValBehavior(sqliteContext.effect, sqliteContext.testRepo, "SQLitePRepo")

  def assertDuplicateKeyValBehavior[F[_]](
    effect: Effect[F], repo: Repo[F, DomainModel], repoName: String): Unit = {

    behavior of s"$repoName.create"

    it should "throw exception on attempt to insert duplicate key val for a primary key" in {

      val origKey = Key("orig")
      val p1 = PrimaryKey(origKey)
      val s1 = effect.run(repo.create(p1))

      try {
        val dkve = intercept[DuplicateKeyValException[_, _]] {
          effect.run(repo.create(p1))
        }
        dkve.p should equal (p1)
        (dkve.key: AnyRef) should equal (PrimaryKey.keySet.head)
      } finally {
        effect.run(repo.delete(s1))
      }
    }

  }

}
