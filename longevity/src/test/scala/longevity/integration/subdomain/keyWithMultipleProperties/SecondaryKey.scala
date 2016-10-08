package longevity.integration.subdomain.keyWithMultipleProperties

import longevity.subdomain.KeyVal

case class SecondaryKey(
  prop1: String,
  prop2: String)
extends KeyVal[KeyWithMultipleProperties, SecondaryKey]
