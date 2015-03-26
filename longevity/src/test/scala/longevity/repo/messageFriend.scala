package longevity.repo

import emblem._
import longevity.context._
import longevity.domain._

object messageFriend {

  case class Friend(name: String) extends RootEntity

  object FriendType extends RootEntityType[Friend]

  case class Message(author: Assoc[Friend], content: String) extends RootEntity

  object MessageType extends RootEntityType[Message]

  val entityTypes = EntityTypePool() + FriendType + MessageType

  val subdomain = Subdomain("blog", entityTypes)

  val shorthandPool = ShorthandPool()

  val boundedContext = BoundedContext(Mongo, subdomain, shorthandPool)

  val inMemBoundedContext = BoundedContext(InMem, subdomain, shorthandPool)

}
