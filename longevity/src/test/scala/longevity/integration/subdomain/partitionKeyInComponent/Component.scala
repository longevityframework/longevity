package longevity.integration.subdomain.partitionKeyInComponent

import longevity.subdomain.annotations.component

@component
case class Component(prop1: String, prop2: String, key: Key)
