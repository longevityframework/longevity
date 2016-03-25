package longevity.unit.persistence

import longevity.context._
import longevity.subdomain.Assoc
import longevity.subdomain.EntityTypePool
import longevity.subdomain.ShorthandPool
import longevity.subdomain.Subdomain
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

object messageFriend {

  implicit val shorthandPool = ShorthandPool.empty

  case class Friend(name: String) extends Root

  object FriendType extends RootType[Friend] {
    object keys {
    }
    object indexes {
    }
  }

  case class Message(author: Assoc[Friend], content: String) extends Root

  object MessageType extends RootType[Message] {
    object keys {
    }
    object indexes {
    }
  }

  object context {
    val entityTypes = EntityTypePool() + FriendType + MessageType
    val subdomain = Subdomain("blog", entityTypes)
    val inMemLongevityContext = LongevityContext(subdomain, InMem)
    val mongoLongevityContext = LongevityContext(subdomain, Mongo)
    val cassandraLongevityContext = LongevityContext(subdomain, Cassandra)
  }

}
