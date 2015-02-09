package emblem.testData

/** for type map happy cases */
object blogs {

  trait Entity
  case class User(uri: String) extends Entity
  case class Blog(uri: String) extends Entity

  trait EntityType[E <: Entity]
  object userType extends EntityType[User]
  object blogType extends EntityType[Blog]

  trait Repo[E <: Entity] {
    var saveCount = 0
    def save(entity: E): Unit = saveCount += 1
  }
  class UserRepo extends Repo[User]
  class BlogRepo extends Repo[Blog]

}
