package longevity.integration.subdomain.keyWithForeignKey

import longevity.subdomain.KeyVal

case class KeyWithForeignKeyId(id: String)
extends KeyVal[KeyWithForeignKey, KeyWithForeignKeyId]
