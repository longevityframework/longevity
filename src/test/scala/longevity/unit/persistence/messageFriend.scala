package longevity.unit.persistence

import longevity.context.Cassandra
import longevity.context.InMem
import longevity.context.LongevityContext
import longevity.context.Mongo
import longevity.subdomain.KeyVal
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.PTypePool
import longevity.subdomain.ptype.RootType

object messageFriend {

  case class FriendId(id: String) extends KeyVal[Friend, FriendId](Friend.keys.id)

  case class Friend(id: FriendId, name: String) extends Root

  object Friend extends RootType[Friend] {
    object props {
      val id = prop[FriendId]("id")
    }
    object keys {
      val id = key(props.id)
    }
    object indexes {
    }
  }

  case class MessageId(id: String) extends KeyVal[Message, MessageId](Message.keys.id)

  case class Message(id: MessageId, author: FriendId, content: String) extends Root

  object Message extends RootType[Message] {
    object props {
      val id = prop[MessageId]("id")
    }
    object keys {
      val id = key(props.id)
    }
    object indexes {
    }
  }

  val subdomain = Subdomain("blog", PTypePool(Friend, Message))
  val inMemLongevityContext = LongevityContext(subdomain, InMem)
  val mongoLongevityContext = LongevityContext(subdomain, Mongo)
  val cassandraLongevityContext = LongevityContext(subdomain, Cassandra)

}
