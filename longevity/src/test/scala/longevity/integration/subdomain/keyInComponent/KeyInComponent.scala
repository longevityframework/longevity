package longevity.integration.subdomain.keyInComponent

import longevity.subdomain.PType
import longevity.subdomain.annotations.mprops

case class KeyInComponent(
  filler: String,
  component: Component)

@mprops object KeyInComponent extends PType[KeyInComponent] {
  object keys {
    val primaryKey = key(props.component.key)
  }
}
