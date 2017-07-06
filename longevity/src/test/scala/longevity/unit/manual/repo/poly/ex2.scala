package longevity.unit.manual.repo.poly

package object ex2 {

import longevity.unit.manual.repo.poly.ex1._

// end prelude

import longevity.context.LongevityContext
import longevity.persistence.Repo
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

val context = LongevityContext[Future, DomainModel]()

val repo: Repo[Future, DomainModel] = context.repo

val user: User = Member(Username("u1"), Email("e1"), 3)
val member: Member = Member(Username("u2"), Email("e2"), 5)
val commenter: Commenter = Commenter(Username("u3"))

import longevity.persistence.PState
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val fpUser: Future[PState[User]] = repo.create(user)
val fpMember: Future[PState[Member]] = repo.create(member)
val fpCommenter: Future[PState[Commenter]] = repo.create(commenter)

}
