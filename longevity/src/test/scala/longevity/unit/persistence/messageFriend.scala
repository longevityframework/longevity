package longevity.unit.persistence

import longevity.TestLongevityConfigs.cassandraConfig
import longevity.TestLongevityConfigs.inMemConfig
import longevity.TestLongevityConfigs.mongoConfig
import longevity.context.LongevityContext
import longevity.subdomain.KeyVal
import longevity.subdomain.Subdomain
import longevity.subdomain.Persistent
import longevity.subdomain.PTypePool
import longevity.subdomain.PType

object messageFriend {

  case class FriendId(id: String) extends KeyVal[Friend, FriendId]

  case class Friend(id: FriendId, name: String) extends Persistent

  object Friend extends PType[Friend] {
    object props {
      val id = prop[FriendId]("id")
    }
    object keys {
      val id = key(props.id)
    }
  }

  case class MessageId(id: String) extends KeyVal[Message, MessageId]

  case class Message(id: MessageId, author: FriendId, content: String) extends Persistent

  object Message extends PType[Message] {
    object props {
      val id = prop[MessageId]("id")
    }
    object keys {
      val id = key(props.id)
    }
  }

  val subdomain = Subdomain("blog", PTypePool(Friend, Message))
  val inMemLongevityContext = new LongevityContext(subdomain, inMemConfig)
  val mongoLongevityContext = new LongevityContext(subdomain, mongoConfig)
  val cassandraLongevityContext = new LongevityContext(subdomain, cassandraConfig)

}
