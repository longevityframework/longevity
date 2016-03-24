package longevity.integration.subdomain.indexWithMultipleProperties

import longevity.subdomain._

case class IndexWithMultipleProperties(
  realm: String,
  name: String)
extends Root

object IndexWithMultipleProperties extends RootType[IndexWithMultipleProperties] {
  object props {
    val realm = prop[String]("realm")
    val name = prop[String]("name")
  }
  object keys {
  }
  object indexes {
    val realmName = index(props.realm, props.name)
  }
}
