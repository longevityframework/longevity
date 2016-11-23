package longevity.integration.subdomain.indexWithMultipleProperties

import longevity.subdomain.annotations.persistent

@persistent(
  keySet = emptyKeySet,
  indexSet = Set(index(props.realm, props.name)))
case class IndexWithMultipleProperties(
  realm: String,
  name: String)
