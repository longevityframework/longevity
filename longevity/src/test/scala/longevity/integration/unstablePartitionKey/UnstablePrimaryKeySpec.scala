package longevity.integration.unstablePrimaryKey

import longevity.TestLongevityConfigs
import longevity.context.Effect
import longevity.context.LongevityContext
import longevity.exceptions.persistence.UnstablePrimaryKeyException
import longevity.integration.model.primaryKey.Key
import longevity.integration.model.primaryKey.PrimaryKey
import longevity.integration.model.primaryKey.DomainModel
import longevity.persistence.Repo
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** expect UnstablePrimaryKeyException when primary key changes */
class UnstablePrimaryKeySpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  val cassandraContext = new LongevityContext[Future, DomainModel](TestLongevityConfigs.cassandraConfig)
  val inMemContext = new LongevityContext[Future, DomainModel](TestLongevityConfigs.inMemConfig)
  val mongoContext = new LongevityContext[Future, DomainModel](TestLongevityConfigs.mongoConfig)
  val sqliteContext = new LongevityContext[Future, DomainModel](TestLongevityConfigs.sqliteConfig)

  override def beforeAll() = {
    cassandraContext.effect.run(cassandraContext.testRepo.createSchema)
    inMemContext.effect.run(inMemContext.testRepo.createSchema)
    mongoContext.effect.run(mongoContext.testRepo.createSchema)
    sqliteContext.effect.run(sqliteContext.testRepo.createSchema)
  }

  assertUnstablePrimaryKeyBehavior(cassandraContext.effect, cassandraContext.testRepo, "CassandraPRepo")
  assertUnstablePrimaryKeyBehavior(inMemContext.effect, inMemContext.testRepo, "InMemPRepo")
  assertUnstablePrimaryKeyBehavior(mongoContext.effect, mongoContext.testRepo, "MongoPRepo")
  assertUnstablePrimaryKeyBehavior(sqliteContext.effect, sqliteContext.testRepo, "SQLitePRepo")

  def assertUnstablePrimaryKeyBehavior[F[_]](
    effect: Effect[F], repo: Repo[F, DomainModel], repoName: String): Unit = {

    behavior of s"$repoName.create"

    it should "throw exception on update with modified primary key" in {

      val origKey = Key("orig")
      val modifiedKey = Key("modified")
      val p1 = PrimaryKey(origKey)
      val p2 = PrimaryKey(modifiedKey)
      val s1 = effect.run(repo.create(p1))

      def expectUnstable(f: F[_]) = {
        val e = intercept[UnstablePrimaryKeyException[_, _]](effect.run(f))
        e.orig should equal (p1)
        e.origKeyVal should equal (origKey)
        e.newKeyVal should equal (modifiedKey)
      }

      try {
        val s2 = s1.set(p2)
        expectUnstable(repo.update(s2))
        expectUnstable(repo.delete(s2))
      } finally {
        effect.run(repo.delete(s1))
      }
    }

  }

}
