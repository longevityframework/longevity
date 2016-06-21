package longevity.integration.duplicateKeyVal

import longevity.exceptions.persistence.DuplicateKeyValException
import longevity.integration.subdomain.allAttributes.AllAttributes
import longevity.integration.subdomain.allAttributes.mongoContext
import longevity.persistence.Repo
import org.joda.time.DateTime
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.SpanSugar._
import scala.concurrent.ExecutionContext.Implicits.global

/** expect mongo to throw DuplicateKeyValException in non-partitioned database
 * setup such as our test database. note we do not necessarily expect the same
 * behavior out of other back ends. we provide no guarantee that duplicate key
 * vals will be caught, as our underlying back ends do not necessarily provide any
 * such guarantee. but Mongo does guarantee to catch this in a single partition
 * database.
 */
class MongoDuplicateKeyValSpec
extends FlatSpec
with GivenWhenThen
with Matchers
with ScalaFutures {

  override implicit def patienceConfig = PatienceConfig(
    timeout = scaled(4000 millis),
    interval = scaled(50 millis))

  assertDuplicateKeyValBehavior(mongoContext.testRepoPool[AllAttributes], "MongoRepo")
  assertDuplicateKeyValBehavior(mongoContext.inMemTestRepoPool[AllAttributes], "InMemRepo")

  def assertDuplicateKeyValBehavior(repo: Repo[AllAttributes], repoName: String): Unit = {

    behavior of s"$repoName.create with a single partitioned database"

    it should "throw exception on attempt to insert duplicate key val" in {

      val uri = "uri must be unique"
      val p1 = AllAttributes(uri, true, 'c', 5.7d, 4.5f, 3, 77l, "stringy", DateTime.now)
      val p2 = AllAttributes(uri, false, 'd', 6.7d, 5.5f, 4, 78l, "stingy", DateTime.now)
      val s1 = repo.create(p1).futureValue

      try {
        val exception = repo.create(p2).failed.futureValue
        if (!exception.isInstanceOf[DuplicateKeyValException[_]]) {
          exception.printStackTrace
        }
        exception shouldBe a [DuplicateKeyValException[_]]

        val dkve = exception.asInstanceOf[DuplicateKeyValException[AllAttributes]]
        dkve.p should equal (p2)
        dkve.key should equal (AllAttributes.keys.uri)
      } finally {
        repo.delete(s1).futureValue
      }
    }

    it should "throw exception on attempt to update to a duplicate key val" in {

      val uri = "uri must be unique 2"
      val p1 = AllAttributes(uri, true, 'c', 5.7d, 4.5f, 3, 77l, "stringy", DateTime.now)
      val p2 = AllAttributes("this one is unique 2", false, 'd', 6.7d, 5.5f, 4, 78l, "stingy", DateTime.now)
      val s1 = repo.create(p1).futureValue
      val s2 = repo.create(p2).futureValue

      try {
        val s2_update = s2.map(_.copy(uri = uri))
        val exception = repo.update(s2_update).failed.futureValue

        if (!exception.isInstanceOf[DuplicateKeyValException[_]]) {
          exception.printStackTrace
        }
        exception shouldBe a [DuplicateKeyValException[_]]
        val dkve = exception.asInstanceOf[DuplicateKeyValException[AllAttributes]]
        dkve.p should equal (s2_update.get)
        dkve.key should equal (AllAttributes.keys.uri)
      } finally {
        repo.delete(s1).futureValue
        repo.delete(s2).futureValue
      }
    }

  }

}
