package longevity.integration.subdomain.keyWithShorthand

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.PType

case class KeyWithShorthand(
  id: KeyWithShorthandId,
  secondaryKey: SecondaryKey)
extends Root

object KeyWithShorthand extends PType[KeyWithShorthand] {
  object props {
    val id = prop[KeyWithShorthandId]("id")
    val secondaryKey = prop[SecondaryKey]("secondaryKey")
  }
  object keys {
    val id = key(props.id)
    val secondaryKey = key(props.secondaryKey)
  }
}
