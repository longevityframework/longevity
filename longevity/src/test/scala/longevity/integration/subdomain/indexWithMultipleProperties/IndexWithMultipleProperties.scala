package longevity.integration.subdomain.indexWithMultipleProperties

import longevity.model.annotations.persistent

@persistent(
  keySet = emptyKeySet,
  indexSet = Set(index(props.realm, props.name)))
case class IndexWithMultipleProperties(
  realm: String,
  name: String)
