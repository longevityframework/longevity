package longevity.integration.subdomain.withComponentWithSinglePropComponent

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComponentWithSinglePropComponent(
  uri: String,
  component: ComponentWithSinglePropComponent)
extends Root

object WithComponentWithSinglePropComponent extends RootType[WithComponentWithSinglePropComponent] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
