package longevity.migrations.integration.basic

import cats.implicits._
import longevity.migrations.integration.LongevityMigrationSpec
import scala.concurrent.Future

abstract class BaseBasicMigrationSpec extends LongevityMigrationSpec[m1.M1, m2.M2] {

  protected def keyspace = "longevity_migrations_test_basic"

  protected def testData = for {
    initialStates     <- createTestData[m1.User](100)
    initialUsers       = initialStates.map(_.get)
  } yield initialUsers

  protected def results(initialUsers: Vector[m1.User]) = for {
    _                 <- context2.repo.openConnection
    initialFinalPairs <- initialUsers.map(initialFinalPair).sequence
    _                 <- context2.repo.closeConnection
    test               = initialFinalPairs.foreach(testPair)
  } yield test

  protected def initialFinalPair(u1: m1.User): Future[(m1.User, m2.User)] = {
    context2.repo.retrieveOne[m2.User](m2.Username(u1.username.value)).map { state2 =>
      (u1, state2.get)
    }
  }

  protected def testPair(pair: (m1.User, m2.User)): Unit = userToUser(pair._1) should equal (pair._2)

}

