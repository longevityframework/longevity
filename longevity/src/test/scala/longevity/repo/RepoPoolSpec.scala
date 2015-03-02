package longevity.repo

import scala.reflect.runtime.universe.TypeTag
import scala.reflect.runtime.universe.typeTag
import org.scalatest._
import testUtil._
import longevity.UnitTest

@UnitTest
class RepoPoolSpec extends FeatureSpec with GivenWhenThen with Matchers {

  feature("The repo pool client can add a repo by entity type") {

    info("A repo adds itself to the repo pool during initialization")

    scenario("attempt to add a repo for an entity type not yet represented in the entity pool") {
      Given("an empty repo pool")
      implicit val repoPool = new RepoPool
      When("a repo adds itself to the repo pool during initialization")
      Then("we can start using the new repo")
      new DummyRepo(UserType)
    }

    scenario("attempt to add a repo for an entity type already represented in the entity pool") {
      Given("a repo pool with a user repository in it")
      implicit val repoPool = new RepoPool
      val userRepo1 = new DummyRepo(UserType)
      When("a user repo adds itself to the repo pool during initialization")
      Then("we get an exception about multiple repos for entity type")
      val thrown = intercept[RepoPool.MultipleReposForEntityType[User]] {
        new DummyRepo(UserType)
      }
      thrown.repo1 should equal (userRepo1)
    }

  }

  feature("The RepoPool client can retrieve repos by entity type") {

    scenario("attempt to retrieve a repo for an entity type not represented in the entity pool") {
      Given("an empty repo pool")
      implicit val repoPool = new RepoPool
      When("we attempt to retrieve a user repo from the pool")
      Then("we get an exception about no repos for entity type")
      val thrown = intercept[RepoPool.NoRepoForEntityType[User]] {
        repoPool.repoForEntityTypeTag(typeTag[User])
      }
      thrown.entityTypeTag should equal (typeTag[User])
    }

    scenario("attempt to retrieve a repo for an entity type represented in the entity pool") {
      Given("a repo pool with a user repository in it")
      implicit val repoPool = new RepoPool
      val userRepo = new DummyRepo(UserType)
      When("we attempt to retrieve a user repo from the pool")
      Then("we get back our user repository")
      val actualRepo = repoPool.repoForEntityTypeTag(typeTag[User])
      actualRepo should equal (userRepo)
    }

  }

}
