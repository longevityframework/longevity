package longevity.integration.model.keyInComponent

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class KeyInComponent(
  filler: String,
  component: Component)

object KeyInComponent {
  implicit val componentKeyKey = key(props.component.key)
}
