package longevity.unit.persistence

import longevity.context._
import longevity.subdomain._

object messageFriend {

  implicit val shorthandPool = ShorthandPool.empty

  case class Friend(name: String) extends RootEntity

  object FriendType extends RootEntityType[Friend]

  case class Message(author: Assoc[Friend], content: String) extends RootEntity

  object MessageType extends RootEntityType[Message]

  object context {
    val entityTypes = EntityTypePool() + FriendType + MessageType
    val subdomain = Subdomain("blog", entityTypes)
    val longevityContext = LongevityContext(subdomain, Mongo)
    val inMemLongevityContext = LongevityContext(subdomain, InMem)
  }

}
