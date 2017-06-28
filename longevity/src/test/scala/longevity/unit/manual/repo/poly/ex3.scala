package longevity.unit.manual.repo.poly

import longevity.unit.manual.repo.poly.ex1._
import longevity.unit.manual.repo.poly.ex2._
import longevity.persistence.PState
import scala.concurrent.Future

object ex3 {

// end prelude

val retrievedUser: Future[Option[PState[User]]] =
  repo.retrieve[User](commenter.username)

val retrievedMember: Future[Option[PState[Member]]] =
  repo.retrieve[Member](member.email)
  
}
