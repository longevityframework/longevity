package longevity.integration.model.primaryKeyInComponent

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class PrimaryKeyInComponent(
  filler: String,
  component: Component)

object PrimaryKeyInComponent {
  implicit val componentKeyKey = primaryKey(props.component.key)
}
