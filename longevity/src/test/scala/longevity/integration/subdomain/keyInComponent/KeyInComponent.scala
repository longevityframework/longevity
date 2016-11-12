package longevity.integration.subdomain.keyInComponent

import longevity.subdomain.PType

case class KeyInComponent(
  filler: String,
  component: Component)

object KeyInComponent extends PType[KeyInComponent] {
  object props {
    val key = prop[Key]("component.key")
  }
  object keys {
    val primaryKey = key(props.key)
  }
}
