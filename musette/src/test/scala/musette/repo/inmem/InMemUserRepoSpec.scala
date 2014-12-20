package musette
package repo.inmem

import org.scalatest._
import org.scalatest.OptionValues._
import longevity.testUtils.InMemRepoSpec
import domain.testUtils._

class InMemUserRepoSpec extends InMemRepoSpec(
  "user",
  new InMemRepoLayer().userRepo,
  testEntityGen.user,
  entityMatchers.persistedUserShouldMatchUnpersisted)

