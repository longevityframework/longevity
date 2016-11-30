package longevity.unit.persistence

import longevity.TestLongevityConfigs.cassandraConfig
import longevity.TestLongevityConfigs.inMemConfig
import longevity.TestLongevityConfigs.mongoConfig
import longevity.context.LongevityContext
import longevity.subdomain.KeyVal
import longevity.subdomain.Subdomain
import longevity.subdomain.PTypePool
import longevity.subdomain.PType

object messageFriend {

  case class FriendId(id: String) extends KeyVal[Friend]

  case class Friend(id: FriendId, name: String)

  object Friend extends PType[Friend] {
    object props {
      val id = prop[FriendId]("id")
    }
    val keySet = Set(key(props.id))
  }

  case class MessageId(id: String) extends KeyVal[Message]

  case class Message(id: MessageId, author: FriendId, content: String)

  object Message extends PType[Message] {
    object props {
      val id = prop[MessageId]("id")
    }
    val keySet = Set(key(props.id))
  }

  val subdomain = Subdomain("blog", PTypePool(Friend, Message))
  val inMemLongevityContext = new LongevityContext(subdomain, inMemConfig)
  val mongoLongevityContext = new LongevityContext(subdomain, mongoConfig)
  val cassandraLongevityContext = new LongevityContext(subdomain, cassandraConfig)

}
