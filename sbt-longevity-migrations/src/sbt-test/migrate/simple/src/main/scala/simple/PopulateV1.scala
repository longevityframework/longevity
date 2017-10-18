package simple

import longevity.context.LongevityContext
import longevity.effect.Blocking
import v1._

object PopulateV1 extends App {
  val context = LongevityContext[Blocking, DomainModel](v1Config)
  val repo = context.repo
  try {
    repo.create(testUser1)
  } finally {
    repo.closeConnection
  }
}
