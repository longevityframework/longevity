package longevity.integration.subdomain.key

import longevity.subdomain.PType
import longevity.subdomain.annotations.mprops

case class Key(id: KeyId)

@mprops object Key extends PType[Key] {
  object keys {
    val id = key(props.id)
  }
}
