package longevity.integration.model.indexWithMultipleProperties

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class IndexWithMultipleProperties(
  realm: String,
  name: String)

object IndexWithMultipleProperties {
  override lazy val indexSet = Set(index(props.realm, props.name))
}
