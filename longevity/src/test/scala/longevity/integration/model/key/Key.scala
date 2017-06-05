package longevity.integration.model.key

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class Key(id: KeyId)

object Key {
  implicit val idKey = key(props.id)
}
