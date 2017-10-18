package simple

import longevity.context.LongevityContext
import longevity.effect.Blocking
import v2._

object CheckV2 extends App {
  val context = LongevityContext[Blocking, DomainModel](v2Config)
  val repo = context.repo
  try {
    val retrieved = repo.retrieveOne[User](testUser2.username)
    if (retrieved.get != testUser2) {
      throw new RuntimeException(s"wot user2? ${retrieved.get}")
    }
  } finally {
    repo.closeConnection
  }
}
