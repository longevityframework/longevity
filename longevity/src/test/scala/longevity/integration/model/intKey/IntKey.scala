package longevity.integration.model.intKey

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class IntKey(id: IntKeyId)

object IntKey {
  implicit val idIntKey = key(props.id)
}
