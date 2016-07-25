package emblem.typeKeyMap

import emblem.TypeKey
import emblem.TypeKeyMap
import emblem.typeKey
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeKeyMap]] when the `Val` has a single type parameter */
class SimpleValuedSpec extends FlatSpec with GivenWhenThen with Matchers {

  import emblem.testData.blogs._
  val userRepo = new CrmUserRepo
  val blogRepo = new CrmBlogRepo

  behavior of "a TypeKeyMap where the value type has a single type parameter"

  it should "only allow key/value pairs with matching type param" in {
    var entityTypeToRepoMap = TypeKeyMap[CrmEntity, CrmRepo]()
    "entityTypeToRepoMap += typeKey[CrmUser] -> userRepo" should compile
    "entityTypeToRepoMap += typeKey[CrmUser] -> blogRepo" shouldNot compile
    "entityTypeToRepoMap += typeKey[CrmBlog] -> userRepo" shouldNot compile
  }

  it should "store multiple key/value pairs for a given type param" in {
    val typeKeySet = Set(typeKey[CrmUser], typeKey[CrmBlog])

    var localEntityStore = TypeKeyMap[CrmEntity, Seq]()
    localEntityStore += (typeKey[CrmUser] -> Seq(CrmUser("user1"), CrmUser("user2"), CrmUser("user3")))
    localEntityStore += (typeKey[CrmBlog] -> Seq(CrmBlog("blog1"), CrmBlog("blog2")))

    var entityTypeToRepoMap = TypeKeyMap[CrmEntity, CrmRepo]()
    entityTypeToRepoMap += userRepo
    entityTypeToRepoMap += blogRepo

    // http://scabl.blogspot.com/2015/01/introduce-type-param-pattern.html
    def saveEntities[E <: CrmEntity : TypeKey]: Unit = {
      val entitySeq = localEntityStore(typeKey)
      val repo = entityTypeToRepoMap(typeKey)
      entitySeq.foreach { entity => repo.save(entity) }
    }

    typeKeySet.foreach { implicit typeKey => saveEntities }

    userRepo.saveCount should equal (3)
    blogRepo.saveCount should equal (2)
  }

}
