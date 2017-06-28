package longevity.integration.duplicateKeyVal

import longevity.TestLongevityConfigs
import longevity.context.Effect
import longevity.context.LongevityContext
import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.integration.model.basics.Basics
import longevity.integration.model.basics.BasicsId
import longevity.integration.model.basics.DomainModel
import longevity.persistence.Repo
import org.joda.time.DateTime
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** expect InMem, MongoDB, and SQLite back ends to throw
 * DuplicateKeyValException in non-partitioned database setup such as
 * our test database.
 *
 * we do not necessarily expect the same behavior out of other
 * back ends. we provide no guarantee that duplicate key vals will be
 * caught, as our underlying back ends do not necessarily provide any
 * such guarantee. but Mongo does guarantee to catch this in a single
 * partition database.
 */
class DuplicateKeyValSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

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

  def assertDuplicateKeyValBehavior[F[_]](effect: Effect[F], repo: Repo[F, DomainModel], repoName: String): Unit = {

    behavior of s"$repoName with a single partitioned database"

    it should "throw exception on attempt to insert duplicate key val" in {

      val id = BasicsId("id must be unique")
      val p1 = Basics(id, true, 'c', 5.7d, 4.5f, 3, 77l, "stringy", DateTime.now)
      val p2 = Basics(id, false, 'd', 6.7d, 5.5f, 4, 78l, "stingy", DateTime.now)
      val s1 = effect.run(repo.create(p1))

      try {
        val dkve = intercept[DuplicateKeyValException[_, _]] {
          effect.run(repo.create(p2))
        }
        dkve.p should equal (p2)
        (dkve.key: AnyRef) should equal (Basics.keySet.head)
      } finally {
        effect.run(repo.delete(s1))
      }
    }

    it should "throw exception on attempt to update to a duplicate key val" in {

      val id = BasicsId("id must be unique 2")
      val p1 = Basics(id, true, 'c', 5.7d, 4.5f, 3, 77l, "stringy", DateTime.now)
      val newId = BasicsId("this one is unique 2")
      val p2 = Basics(newId, false, 'd', 6.7d, 5.5f, 4, 78l, "stingy", DateTime.now)
      val s1 = effect.run(repo.create(p1))
      val s2 = effect.run(repo.create(p2))

      try {
        val s2_update = s2.map(_.copy(id = id))
        val dkve = intercept[DuplicateKeyValException[_, _]] {
          effect.run(repo.update(s2_update))
        }
        dkve.p should equal (s2_update.get)
        (dkve.key: AnyRef) should equal (Basics.keySet.head)
      } finally {
        effect.run(repo.delete(s1))
        effect.run(repo.delete(s2))
      }
    }

  }

}
