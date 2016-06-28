package longevity.integration.subdomain.multipleKeys

import longevity.subdomain.KeyVal

case class Username(username: String)
extends KeyVal[MultipleKeys](MultipleKeys.keys.username)
