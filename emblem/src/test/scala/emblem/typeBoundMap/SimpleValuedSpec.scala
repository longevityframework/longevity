package emblem.typeBoundMap

import emblem.TypeKey
import emblem.typeBound.TypeBoundMap
import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** specifications for [[TypeKeyMap]] when the `Key` and `Val` type have a single shared type parameter */
class SimpleValuedSpec extends FlatSpec with GivenWhenThen with Matchers {

  behavior of "a TypeBoundMap where both key and value have a single shared type parameter"

  import emblem.testData.blogs._
  val userRepo = new CrmUserRepo
  val blogRepo = new CrmBlogRepo

  it should "only allow key/value pairs with matching type param" in {
    var entityTypeToRepoMap = TypeBoundMap[CrmEntity, CrmEntityType, CrmRepo]()
    "entityTypeToRepoMap += userType -> userRepo" should compile
    "entityTypeToRepoMap += userType -> blogRepo" shouldNot compile
    "entityTypeToRepoMap += blogType -> userRepo" shouldNot compile
  }

  it should "store multiple key/value pairs for a given type param" in {
    val entityTypeSet = Set(userType, blogType)

    var localEntityStore = TypeBoundMap[CrmEntity, CrmEntityType, Seq]()
    localEntityStore += (userType -> Seq(CrmUser("user1"), CrmUser("user2"), CrmUser("user3")))
    localEntityStore += (blogType -> Seq(CrmBlog("blog1"), CrmBlog("blog2")))

    var entityTypeToRepoMap = TypeBoundMap[CrmEntity, CrmEntityType, CrmRepo]()
    entityTypeToRepoMap += userType -> userRepo
    entityTypeToRepoMap += blogType -> blogRepo

    // http://scabl.blogspot.com/2015/01/introduce-type-param-pattern.html
    def saveEntities[E <: CrmEntity : TypeKey](entityType: CrmEntityType[E]): Unit = {
      val entities = localEntityStore(entityType)
      val repo = entityTypeToRepoMap(entityType)
      entities.foreach { entity => repo.save(entity) }
    }

    entityTypeSet.foreach { entityType => saveEntities(entityType) }

    userRepo.saveCount should equal (3)
    blogRepo.saveCount should equal (2)
  }
 
}
