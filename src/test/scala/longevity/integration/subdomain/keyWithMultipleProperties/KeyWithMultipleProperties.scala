package longevity.integration.subdomain.keyWithMultipleProperties

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class KeyWithMultipleProperties(
  realm: String,
  name: String)
extends Root

object KeyWithMultipleProperties extends RootType[KeyWithMultipleProperties] {
  object props {
    val realm = prop[String]("realm")
    val name = prop[String]("name")
  }
  object keys {
    val realmName = key(props.realm, props.name)
  }
  object indexes {
  }
}
