package longevity.unit.persistence

import longevity.TestLongevityConfigs.cassandraConfig
import longevity.TestLongevityConfigs.inMemConfig
import longevity.TestLongevityConfigs.mongoConfig
import longevity.context.LongevityContext
import longevity.model.KeyVal
import longevity.model.DomainModel
import longevity.model.PTypePool
import longevity.model.PType

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

  val domainModel = DomainModel(PTypePool(Friend, Message))
  val inMemLongevityContext = new LongevityContext(domainModel, inMemConfig)
  val mongoLongevityContext = new LongevityContext(domainModel, mongoConfig)
  val cassandraLongevityContext = new LongevityContext(domainModel, cassandraConfig)

}
